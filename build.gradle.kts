import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    `maven-publish`
}

group = "com.github.ichanzhar"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("cz.jirutka.rsql:rsql-parser:2.1.0")
    implementation("joda-time:joda-time:2.10.6")
    implementation("org.hibernate:hibernate-core:5.4.21.Final")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.springframework.data:spring-data-jpa:2.2.9.RELEASE")
    implementation("org.slf4j:slf4j-ext:1.7.30")
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}