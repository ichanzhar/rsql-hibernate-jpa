import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.20"
    `maven-publish`
    signing
}

group = "com.github.ichanzhar"
version = "4.0.0"

repositories {
    mavenCentral()
}

val hibernate = "7.4.1.Final"
val dataJpa = "4.1.0"
val slf4jV = "2.0.18"

dependencies {
    api("cz.jirutka.rsql:rsql-parser:2.1.0")
    implementation("org.hibernate.orm:hibernate-core:$hibernate")
    api("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.springframework.data:spring-data-jpa:$dataJpa")
    implementation("org.slf4j:slf4j-ext:$slf4jV")
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
}


tasks.withType<KotlinCompile>() {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar.get())
            pom {
                name.set("RSQL Hibernate JPA")
                description.set("RSQL implementation for Hibernate/Spring data with join tables support")
                url.set("https://github.com/ichanzhar/rsql-hibernate-jpa")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("ichanzhar")
                        name.set("Ihor Chanzhar")
                        email.set("ihor.chanzhar@gmail.com")
                        organization.set("com.github.ichanzhar")
                        organizationUrl.set("https://github.com/ichanzhar")
                    }
                }
                contributors {
                    contributor {
                        name.set("Oleksandr Hubenko")
                        email.set("oleksandr.hubenko47@gmail.com")
                        url.set("https://github.com/ohubenko")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ichanzhar/rsql-hibernate-jpa.git")
                    developerConnection.set("scm:git:git@github.com:ichanzhar/rsql-hibernate-jpa.git")
                    url.set("https://github.com/ichanzhar/rsql-hibernate-jpa")
                }
            }
        }
    }
}

signing {
    val signingKey = providers.environmentVariable("SIGNING_KEY").orNull
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, providers.environmentVariable("SIGNING_PASSWORD").orNull)
    }
    sign(publishing.publications.getByName("mavenJava"))
}
