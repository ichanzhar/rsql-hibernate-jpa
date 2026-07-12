# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

RSQL implementation for Hibernate/Spring Data JPA that translates RSQL query strings into Spring Data JPA
`Specification`s, with support for joined/associated tables. Published to Maven Central as
`com.github.ichanzhar:rsql-hibernate-jpa`. Requires JDK 21, Hibernate 7.4.x, Spring Data JPA 4.1.x
(see `rsql-hibernate-jpa/build.gradle.kts` for exact versions).

This repo is a monorepo: the library lives in `rsql-hibernate-jpa/` (the only subproject included
from the root `settings.gradle.kts`). `examples/` holds example projects demonstrating usage —
each is a fully independent Gradle build (own wrapper, own `settings.gradle.kts`). Examples are
NOT included in the root build and are NOT part of root CI; build/test them from their own
directory. `spring-boot-postgres-example/` (Spring Boot 3.5) depends on the released `0.21`
Maven Central artifact; `spring-boot4-postgres-example/` (Spring Boot 4.1) declares the `7.0.8`
coordinate but substitutes it with the local library source via `includeBuild("../..")` in its
`settings.gradle.kts` — delete that block once `7.0.8` is published.

The library module (`rsql-hibernate-jpa/`) has no test source set. Both examples have their own
Testcontainers-based integration tests (Docker required).

## Common commands

Library (run from repo root — root build includes exactly one subproject, `rsql-hibernate-jpa`):

```bash
./gradlew build          # compile, assemble jar + sources/javadoc jars for rsql-hibernate-jpa
./gradlew compileKotlin   # compile only
./gradlew clean
```

CI (`.github/workflows/pr-ci.yaml`) runs `./gradlew build` on JDK 21 for every PR — this builds only
the library module.

Publishing to Sonatype/Maven Central is done via `./gradlew publish`, which requires `ossUsername`/`ossPassword`
project properties and GPG signing configured — not needed for normal development.

Example apps (independent builds, require Docker for tests; try either example directory):

```bash
cd examples/spring-boot-postgres-example   # or examples/spring-boot4-postgres-example
./gradlew test      # spins up Postgres via Testcontainers
./gradlew bootRun   # requires a running Postgres instance, see its own README
```

## Architecture

All paths below are relative to `rsql-hibernate-jpa/src/main/kotlin/com/github/ichanzhar/rsql/`.

The library turns an RSQL string (parsed by `cz.jirutka.rsql:rsql-parser`) into a JPA `Specification<E>`. Flow:

1. **`utils/RsqlParserFactory`** builds an `RSQLParser` configured with the set of operators currently registered
   in `utils/RsqlOperationsRegistry`. Callers pick a `ParserContext` (`COMMON` or `POSTGRESQL`); `POSTGRESQL`
   additionally registers the `=jsoneq=`/`=jsonbeq=` operators.
2. Parsing an RSQL string yields an AST (`Node`) that is fed to **`JpaRsqlVisitor`**, which delegates to
   **`GenericRsqlSpecBuilder`** to recursively turn `LogicalNode` (AND/OR) and `ComparisonNode` nodes into a
   combined `Specification<E>`.
3. Each `ComparisonNode` becomes a **`JpaRsqlSpecification`**, which:
   - Splits the selector on `.` to detect join paths (e.g. `address.city`) and builds left joins via
     `Root.join`/`Join.join` when the property is an association or embedded attribute.
   - Casts each raw string argument to the target property's Java type via `utils/ArgumentConvertor`
     (ints, dates, UUIDs, enums, etc. — falls back to the raw string on any parse failure), using
     `utils/JavaTypeUtil` to normalize primitive types to their wrapper classes first.
   - Looks up the `ComparisonOperator` in `RsqlOperationsRegistry.operationProcessors` via
     `operations/ProcessorsFactory` to obtain a `Processor` and calls `process()` to get the final `Predicate`.
4. **`operations/*Processor`** classes (one per operator: `Equal`, `NotEqual`, `Gt`, `Gte`, `Lt`, `Lte`, `In`,
   `NotIn`, `IsNull`, `EqualCi`, `IsEmpty`, plus Postgres-only `JsonEqual`/`JsonbEqual`) implement `Processor`
   and build the actual `CriteriaBuilder` predicate. Each extends `AbstractProcessor`, which provides shared
   helpers for detecting whether `params.root` is currently a collection join (`SetJoin`/`ListJoin`/
   `CollectionJoin`/root-as-`JpaRoot` with a collection attribute) and for wildcard (`*text*` → SQL `LIKE`)
   argument handling.
5. **`RsqlOperation`** is the enum source of truth mapping each supported operator symbol to its
   `ComparisonOperator` and `ParserContext`; `RsqlOperationsRegistry` is the mutable, extensible registry that
   actually drives parsing/dispatch and can be extended at runtime via `registerOperation(operator, processor)`
   for custom operators.

### Extending with custom operators

Register a new `ComparisonOperator` and a `Params -> Processor` lambda via
`RsqlOperationsRegistry.registerOperation(...)` before building the parser — see
`RsqlOperationsRegistry.initDefaultPostgresOperation()` for the pattern used for the built-in Postgres
JSON operators.

### Error handling

Argument coercion failures for enums/dates raise `InvalidEnumValueException` / `InvalidDateFormatException`
(package `exception/`); all other cast failures in `ArgumentConvertor` silently fall back to the raw string
argument rather than throwing.