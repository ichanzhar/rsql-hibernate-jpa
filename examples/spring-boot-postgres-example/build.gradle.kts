import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
}

group = "com.github.ichanzhar.rsql.example"
version = "0.1.0"

// The library requires Hibernate 7.x (e.g. org.hibernate.query.criteria.JpaRoot), but Spring
// Boot 3.5.x's own BOM manages Hibernate 6.6.x/Jakarta Persistence 3.1. Override both
// BOM-managed versions so the app runs Hibernate 7's actual required Jakarta Persistence API
// (3.2.x) instead of silently downgrading it and hitting NoClassDefFoundError at runtime.
extra["hibernate.version"] = "7.1.0.Final"
extra["jakarta-persistence.version"] = "3.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Consumed as a real external Maven Central dependency, exactly as any consumer of the
    // library would, not as a Gradle project()/includeBuild reference to the sibling module.
    implementation("com.github.ichanzhar:rsql-hibernate-jpa:0.21")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
