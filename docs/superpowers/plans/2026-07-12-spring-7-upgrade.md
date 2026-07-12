# Spring Framework 7 / Spring Boot 4.1 Upgrade Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Upgrade the library to the Spring Framework 7.0.8 line (as version 7.0.8) and add a Spring Boot 4.1.0 example that consumes it from local source until release.

**Architecture:** Two independent Gradle builds change: the root build (library `rsql-hibernate-jpa/`) gets new versions and a Gradle 9.6.1 wrapper; a new standalone example build `examples/spring-boot4-postgres-example/` mirrors the existing Boot 3.5 example but on Boot 4.1.0, wired to the library source via a Gradle composite build (`includeBuild("../..")`). The existing example stays untouched.

**Tech Stack:** Kotlin 2.3.20, Gradle 9.6.1, JDK 21, Hibernate ORM 7.4.1.Final, Spring Data JPA 4.1.0, Spring Boot 4.1.0, Testcontainers/Postgres.

**Spec:** `docs/superpowers/specs/2026-07-12-spring-7-upgrade-design.md`

## Global Constraints

- Library version: `7.0.8` (tracks Spring Framework version).
- Kotlin `2.3.20` everywhere (library and new example).
- Gradle wrapper `9.6.1` (root; the new example copies the old example's wrapper, already 9.6.1).
- Java baseline 21: `JavaVersion.VERSION_21` / `JvmTarget.JVM_21`.
- Library deps: `hibernate-core:7.4.1.Final`, `spring-data-jpa:4.1.0`; `rsql-parser:2.1.0`, `commons-lang3:3.18.0`, `slf4j-ext:2.0.17` unchanged.
- `examples/spring-boot-postgres-example/` must not be modified.
- Library source changes limited to what compilation forces — no refactoring.
- Do not write code comments (user rule).
- The library module has no test source set; its verification is `./gradlew build` plus the new example's integration test.
- Docker is required for `./gradlew test` in the example (Testcontainers).

---

### Task 1: Library build upgrade

**Files:**
- Modify: `rsql-hibernate-jpa/build.gradle.kts:5,11,17-18,31,37`
- Modify (only if compile fails, see Step 3): `rsql-hibernate-jpa/src/main/kotlin/com/github/ichanzhar/rsql/JpaRsqlSpecification.kt:28-29`

**Interfaces:**
- Consumes: nothing.
- Produces: library artifact `com.github.ichanzhar:rsql-hibernate-jpa:7.0.8` built against Hibernate 7.4.1.Final / Spring Data JPA 4.1.0, consumed by Task 3 via composite build.

- [ ] **Step 1: Update versions in `rsql-hibernate-jpa/build.gradle.kts`**

Apply these five line edits (line numbers from current file):

```kotlin
    kotlin("jvm") version "2.3.20"
```
replaces `kotlin("jvm") version "2.2.10"` (line 5).

```kotlin
version = "7.0.8"
```
replaces `version = "0.21"` (line 11).

```kotlin
val hibernate = "7.4.1.Final"
val dataJpa = "4.1.0"
```
replaces `val hibernate = "7.1.0.Final"` / `val dataJpa = "3.5.3"` (lines 17-18).

```kotlin
    sourceCompatibility = JavaVersion.VERSION_21
```
replaces `sourceCompatibility = JavaVersion.VERSION_17` (line 31).

```kotlin
        jvmTarget.set(JvmTarget.JVM_21)
```
replaces `jvmTarget.set(JvmTarget.JVM_17)` (line 37).

- [ ] **Step 2: Build**

Run from repo root: `./gradlew build`

Expected: either `BUILD SUCCESSFUL`, or a compile error in `JpaRsqlSpecification.kt` of the form `'toPredicate' overrides nothing` / nullability mismatch on the `query` parameter (Spring Data JPA 4.x declares `Specification.toPredicate(Root<T>, CriteriaQuery<?>, CriteriaBuilder)` with JSpecify null-marking, so the `query` parameter is non-nullable in Kotlin).

- [ ] **Step 3: Fix override nullability (only if Step 2 failed)**

In `rsql-hibernate-jpa/src/main/kotlin/com/github/ichanzhar/rsql/JpaRsqlSpecification.kt` replace lines 28-29:

```kotlin
    override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): Predicate? {
		if(distinct) query?.distinct(true)
```

with:

```kotlin
    override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): Predicate? {
		if(distinct) query.distinct(true)
```

If other compile errors surface (e.g. Hibernate 7.4 renames), fix each minimally — change only the failing reference, no restructuring. Any error that forces an API-visible change to the library (public signatures) is a stop-and-ask situation, not a silent fix.

- [ ] **Step 4: Verify build passes**

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL`; `rsql-hibernate-jpa/build/libs/` contains `rsql-hibernate-jpa-7.0.8.jar`, `-sources.jar`, `-javadoc.jar`.

- [ ] **Step 5: Commit**

```bash
git add rsql-hibernate-jpa
git commit -m "Upgrade library to Spring Data JPA 4.1.0 / Hibernate 7.4.1, Kotlin 2.3.20, Java 21, version 7.0.8"
```

---

### Task 2: Root Gradle wrapper 9.6.1

**Files:**
- Modify: `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-wrapper.jar`, `gradlew`, `gradlew.bat`

**Interfaces:**
- Consumes: Task 1 (Kotlin 2.3.20 plugin — required, Kotlin 2.2.10 does not support Gradle 9).
- Produces: root build running on Gradle 9.6.1, required by Task 3's `includeBuild("../..")` (an included build runs under the including build's Gradle version, but its own wrapper should match).

- [ ] **Step 1: Regenerate wrapper**

Run from repo root:

```bash
./gradlew wrapper --gradle-version 9.6.1
./gradlew wrapper
```

(The second run executes under 9.6.1 and regenerates `gradlew`/`gradle-wrapper.jar` from the new distribution.)

- [ ] **Step 2: Verify version and build**

Run: `./gradlew --version`
Expected: `Gradle 9.6.1`.

Run: `./gradlew build`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add gradlew gradlew.bat gradle/wrapper
git commit -m "Upgrade Gradle wrapper to 9.6.1"
```

---

### Task 3: Scaffold examples/spring-boot4-postgres-example

**Files:**
- Create: `examples/spring-boot4-postgres-example/` — `src/` copied verbatim from `examples/spring-boot-postgres-example/src/`, wrapper files copied, plus new `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`, `README.md`
- Modify (in the copy only): `examples/spring-boot4-postgres-example/src/test/kotlin/com/github/ichanzhar/rsql/example/web/BookControllerIntegrationTest.kt` (one import)

**Interfaces:**
- Consumes: `com.github.ichanzhar:rsql-hibernate-jpa:7.0.8` from Task 1, substituted to local source via `includeBuild`.
- Produces: a compiling standalone example project; Task 4 runs its tests.

- [ ] **Step 1: Copy skeleton from the existing example**

```bash
cd examples
mkdir spring-boot4-postgres-example
cp -R spring-boot-postgres-example/src spring-boot4-postgres-example/src
cp -R spring-boot-postgres-example/gradle spring-boot4-postgres-example/gradle
cp spring-boot-postgres-example/gradlew spring-boot-postgres-example/gradlew.bat spring-boot-postgres-example/gradle.properties spring-boot4-postgres-example/
cd ..
```

Do NOT copy `build/`, `build.gradle.kts`, `settings.gradle.kts`, or `README.md`.

- [ ] **Step 2: Write `examples/spring-boot4-postgres-example/settings.gradle.kts`**

```kotlin
rootProject.name = "spring-boot4-postgres-example"

includeBuild("../..") {
    dependencySubstitution {
        substitute(module("com.github.ichanzhar:rsql-hibernate-jpa")).using(project(":rsql-hibernate-jpa"))
    }
}
```

- [ ] **Step 3: Write `examples/spring-boot4-postgres-example/build.gradle.kts`**

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("org.springframework.boot") version "4.1.0"
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.spring") version "2.3.20"
}

group = "com.github.ichanzhar.rsql.example"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.github.ichanzhar:rsql-hibernate-jpa:7.0.8")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

Notes locked in by the spec: no `io.spring.dependency-management` plugin (Boot 4 native platform via `SpringBootPlugin.BOM_COORDINATES`); no `extra["hibernate.version"]` overrides (Boot 4.1.0 manages Hibernate 7.4.1.Final, matching the library); Boot 4 starter names `spring-boot-starter-webmvc` / `spring-boot-starter-webmvc-test`; Jackson 3 Kotlin module `tools.jackson.module:jackson-module-kotlin`.

- [ ] **Step 4: Fix the Boot 4 test import in the copied test**

In `examples/spring-boot4-postgres-example/src/test/kotlin/com/github/ichanzhar/rsql/example/web/BookControllerIntegrationTest.kt` replace:

```kotlin
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
```

with:

```kotlin
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
```

(Boot 4 modularization moved it; `@SpringBootTest`, `@ServiceConnection`, `MockMvc` and the Testcontainers imports are unchanged.)

- [ ] **Step 5: Compile (no tests yet)**

```bash
cd examples/spring-boot4-postgres-example
./gradlew compileTestKotlin
```

Expected: `BUILD SUCCESSFUL` (the composite build compiles the library first). If imports from Boot-4-moved packages fail in main sources, fix each import to its `org.springframework.boot.<module>` location — the main sources copied here use only `@SpringBootApplication`, `CommandLineRunner`, `@Profile`, Spring MVC annotations, and JPA annotations, all unmoved, so no changes are expected.

- [ ] **Step 6: Write `examples/spring-boot4-postgres-example/README.md`**

Copy `examples/spring-boot-postgres-example/README.md`, then apply exactly these changes:

Title (line 1) becomes:

```markdown
# spring-boot4-postgres-example
```

Intro sentence (line 3) starts with `A Spring Boot 4.1 + PostgreSQL app demonstrating` instead of `A Spring Boot + PostgreSQL app demonstrating`.

Replace the second paragraph (the one describing the Maven Central dependency, lines 9-12) with:

```markdown
This is a **standalone Gradle project** with its own wrapper and `settings.gradle.kts` — it is
not part of the parent repo's root build or CI. It declares the library by its normal Maven
coordinate (`com.github.ichanzhar:rsql-hibernate-jpa:7.0.8`), but until 7.0.8 is published to
Maven Central, `settings.gradle.kts` substitutes that coordinate with the library source in this
repository via a Gradle composite build (`includeBuild("../..")`). Once 7.0.8 is released,
delete the `includeBuild` block and the example becomes a pure Maven Central consumer that can
be copied out of this repository and run on its own.
```

Requirements section: `- JDK 17+` becomes `- JDK 21+`.

Everything else (domain description, query catalogue, run instructions) is copied unchanged.

- [ ] **Step 7: Commit**

```bash
git add examples/spring-boot4-postgres-example
git commit -m "Add Spring Boot 4.1 Postgres example consuming the library via composite build"
```

---

### Task 4: Integration test run

**Files:**
- Modify: only if the test run surfaces failures; otherwise none.

**Interfaces:**
- Consumes: Task 3's example project.
- Produces: end-to-end proof that library 7.0.8 works on Boot 4.1 (the spec's acceptance gate).

- [ ] **Step 1: Check Docker is available**

Run: `docker info > /dev/null && echo ok`
Expected: `ok`. If Docker is not running, stop and report — the test cannot run without it.

- [ ] **Step 2: Run the integration test**

```bash
cd examples/spring-boot4-postgres-example
./gradlew test
```

Expected: `BUILD SUCCESSFUL`, all tests in `BookControllerIntegrationTest` pass (association joins, scalar operators, `=jsonbeq=`, element collections, embedded attributes — the full query catalogue).

- [ ] **Step 3: If a test fails**

Use superpowers:systematic-debugging. Likely fault lines: Boot 4 test-slice wiring (missing `spring-boot-starter-webmvc-test`), Jackson 3 serialization of the domain model, or a genuine library incompatibility with Hibernate 7.4 predicate APIs. Library fixes must stay minimal (Task 1 Step 3 rules apply); example fixes stay inside `examples/spring-boot4-postgres-example/`.

- [ ] **Step 4: Commit (only if Step 3 changed files)**

```bash
git add -A examples/spring-boot4-postgres-example rsql-hibernate-jpa
git commit -m "Fix Boot 4.1 integration fallout"
```

---

### Task 5: Documentation updates

**Files:**
- Modify: `README.md:6,12-17,19-21`
- Modify: `CLAUDE.md` (Project overview section)

**Interfaces:**
- Consumes: final state of Tasks 1-4.
- Produces: docs matching the shipped versions.

- [ ] **Step 1: Update root `README.md`**

Line 6 becomes:

```markdown
* RSQL implementation for Hibernate/Spring data with join tables support (JDK 21, Spring Framework 7 / Spring Boot 4.x; use version 0.21 for Spring Boot 3.x)
```

The examples bullet (lines 12-17) becomes:

```markdown
* [`examples/`](examples) — example projects demonstrating library usage. Each example is a
  fully independent, standalone Gradle build (own wrapper, own `settings.gradle.kts`) — not part
  of this repo's root build or CI. [`examples/spring-boot-postgres-example`](examples/spring-boot-postgres-example)
  is a Spring Boot 3.5 + Postgres demo depending on the released `0.21` artifact;
  [`examples/spring-boot4-postgres-example`](examples/spring-boot4-postgres-example) is the same
  demo on Spring Boot 4.1, consuming the library source via a Gradle composite build until
  `7.0.8` is published to Maven Central. Both cover core RSQL operators, join-path filtering,
  and the Postgres-only `=jsonbeq=` operator.
```

Dependencies list (lines 20-21) becomes:

```markdown
* Hibernate Core 7.4.1.Final
* Spring Data JPA 4.1.0
```

- [ ] **Step 2: Update `CLAUDE.md`**

In the Project overview section, replace:

```markdown
`com.github.ichanzhar:rsql-hibernate-jpa`. Requires JDK 17, Hibernate 7.x, Spring Data JPA 3.5.x
```

with:

```markdown
`com.github.ichanzhar:rsql-hibernate-jpa`. Requires JDK 21, Hibernate 7.4.x, Spring Data JPA 4.1.x
```

Replace:

```markdown
each is a fully independent Gradle build (own wrapper, own `settings.gradle.kts`) that depends on
the library as an external Maven Central artifact. Examples are NOT included in the root build
and are NOT part of root CI; build/test them from their own directory.

The library module (`rsql-hibernate-jpa/`) has no test source set. The example under
`examples/spring-boot-postgres-example/` does have its own integration test (Testcontainers-based).
```

with:

```markdown
each is a fully independent Gradle build (own wrapper, own `settings.gradle.kts`). Examples are
NOT included in the root build and are NOT part of root CI; build/test them from their own
directory. `spring-boot-postgres-example/` (Spring Boot 3.5) depends on the released `0.21`
Maven Central artifact; `spring-boot4-postgres-example/` (Spring Boot 4.1) declares the `7.0.8`
coordinate but substitutes it with the local library source via `includeBuild("../..")` in its
`settings.gradle.kts` — delete that block once `7.0.8` is published.

The library module (`rsql-hibernate-jpa/`) has no test source set. Both examples have their own
Testcontainers-based integration tests (Docker required).
```

- [ ] **Step 3: Verify no other stale version references**

Run: `grep -rn "0\.21\|VERSION_17\|JDK 17\|3\.5\.3\|7\.1\.0" README.md CLAUDE.md rsql-hibernate-jpa --include="*.kts" --include="*.md"`
Expected: no hits except historical mentions intentionally kept (the root README line 6 "use version 0.21 for Spring Boot 3.x" and the CLAUDE.md sentence about the old example's `0.21` dependency).

- [ ] **Step 4: Commit**

```bash
git add README.md CLAUDE.md
git commit -m "Update docs for 7.0.8 / Spring Boot 4.1"
```
