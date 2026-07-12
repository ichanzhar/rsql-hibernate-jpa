# Spring Framework 7 / Spring Boot 4.1 Upgrade Design

Date: 2026-07-12
Branch: feat/sb4

## Goal

Upgrade the `rsql-hibernate-jpa` library to the Spring Framework 7.0.8 dependency line and add a
second example project on Spring Boot 4.1.0 that consumes the library from local source until the
new version is published to Maven Central.

## Versioning decision

The library's published version becomes **4.0.0**, tracking the Spring Boot major version it targets (0.1x = Spring Boot 2, 0.2x = Spring Boot 3, 4.x = Spring Boot 4). Future releases follow the same scheme. (Amended 2026-07-12: originally 7.0.8 tracking Spring Framework.)

## Version matrix

| Component | Current | Target | Rationale |
|---|---|---|---|
| Library version | 0.21 | 4.0.0 | Tracks Spring Boot major version |
| Kotlin (library + new example) | 2.2.10 | 2.3.20 | Requested |
| Gradle wrapper (root) | 8.14.2 | 9.6.1 | Requested; existing example already ships 9.6.1 |
| Java baseline | 17 | 21 | Requested minimum |
| hibernate-core | 7.1.0.Final | 7.4.1.Final | Version managed by Spring Boot 4.1.0 |
| spring-data-jpa | 3.5.3 | 4.1.0 | Version managed by Spring Boot 4.1.0 (Spring Data 2026.0 train, built on Spring Framework 7.0.8) |
| rsql-parser | 2.1.0 | 2.1.0 | Unchanged |
| commons-lang3 | 3.18.0 | 3.18.0 | Unchanged |
| slf4j-ext | 2.0.17 | 2.0.17 | Unchanged |

Explicit version pins, no Spring BOM imports: consumers on Boot 4.1 get exactly the versions
Boot manages, and the library build stays auditable.

## Part 1: Library upgrade (`rsql-hibernate-jpa/`)

Build changes in `rsql-hibernate-jpa/build.gradle.kts`:

- `version = "4.0.0"`
- `kotlin("jvm") version "2.3.20"`
- `val hibernate = "7.4.1.Final"`, `val dataJpa = "4.1.0"`
- `sourceCompatibility = JavaVersion.VERSION_21`, `jvmTarget.set(JvmTarget.JVM_21)`

Root Gradle wrapper upgraded to 9.6.1 (`./gradlew wrapper --gradle-version 9.6.1`).

Source changes: only what compilation forces. Spring Data JPA 4.x keeps the classic
`Specification<T>` interface (`toPredicate(root, query, cb)`) and its `and`/`or` combinators;
the library does not use removed static factories (`Specification.where`). Expected adjustments
are limited to nullability tweaks in `JpaRsqlSpecification` / `GenericRsqlSpecBuilder` overrides
and any Hibernate 7.4 API renames, discovered via `./gradlew build` and fixed minimally. No
refactoring beyond that.

CI (`.github/workflows/pr-ci.yaml`) already runs on JDK 21 — unchanged.

## Part 2: New example (`examples/spring-boot4-postgres-example/`)

An independent Gradle build mirroring `examples/spring-boot-postgres-example/`: same domain
model (Book, Author, Category, Chapter, Review, Dimensions), `BookRepository`, `BookController`
RSQL search endpoint, `DemoDataSeeder`, `application.yml`, Testcontainers-based
`BookControllerIntegrationTest`, own Gradle 9.6.1 wrapper, own README.

Build file differences from the old example:

- `org.springframework.boot` plugin `4.1.0`; the `io.spring.dependency-management` plugin is
  dropped in favor of Boot 4's recommended native Gradle platform:
  `implementation(platform(SpringBootPlugin.BOM_COORDINATES))`.
- Kotlin `2.3.20`, `kotlin("plugin.spring") version "2.3.20"`, Java 21.
- Boot 4 starter names: `spring-boot-starter-webmvc` (replaces `spring-boot-starter-web`),
  `spring-boot-starter-data-jpa`, and the Boot 4 test starters
  (`spring-boot-starter-webmvc-test` / `spring-boot-starter-test` as applicable, plus
  `spring-boot-testcontainers`). Exact names confirmed against the Boot 4.1.0 managed
  dependency list during implementation.
- No `extra["hibernate.version"]` / `extra["jakarta-persistence.version"]` overrides — Boot
  4.1.0 already manages Hibernate 7.4.1.Final, matching the library.

Dependency wiring until 4.0.0 is on Maven Central:

- Dependency declared with the real coordinate
  `implementation("com.github.ichanzhar:rsql-hibernate-jpa:4.0.0")`.
- `settings.gradle.kts` adds `includeBuild("../..")` with dependency substitution mapping
  `com.github.ichanzhar:rsql-hibernate-jpa` to the `:rsql-hibernate-jpa` subproject, so the
  example builds against local source.
- After the 4.0.0 release: delete the `includeBuild` block; the example becomes a pure Maven
  Central consumer like the existing one. The example README documents this switch.

## Part 3: Unchanged pieces

- `examples/spring-boot-postgres-example/` stays exactly as-is (Boot 3.5.16, library 0.21 from
  Maven Central) as the Boot 3.x usage reference.
- Root `settings.gradle.kts` still includes only `rsql-hibernate-jpa`; examples remain outside
  the root build and root CI.

## Part 4: Documentation updates

- Root `README.md`: requirements (JDK 21, Hibernate 7.4.x, Spring Data JPA 4.1.x), current
  version 4.0.0, examples list mentioning both example projects.
- `CLAUDE.md`: version requirements and the new example.
- New example `README.md`: bootRun and test instructions, Docker requirement, note about the
  temporary `includeBuild` wiring.

## Verification

1. `./gradlew build` at repo root — library compiles, jars assemble on Gradle 9.6.1 / Kotlin
   2.3.20 / JDK 21.
2. `cd examples/spring-boot4-postgres-example && ./gradlew test` — Testcontainers integration
   test passes against the locally built library (requires Docker). This is the end-to-end
   proof the upgrade works on Boot 4.1.
3. Existing example untouched; no verification needed there.

## Error handling

No changes to the library's error-handling behavior (`InvalidEnumValueException`,
`InvalidDateFormatException`, silent string fallback in `ArgumentConvertor`).

## Out of scope

- Publishing 4.0.0 to Maven Central (manual `./gradlew publish` with credentials, as today).
- Removing the `includeBuild` wiring after release.
- Any feature work or refactoring in library sources.
