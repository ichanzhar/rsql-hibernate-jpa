import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    signing
}

group = "com.github.ichanzhar"
version = "0.21"

repositories {
    mavenCentral()
}

val hibernate = "6.4.4.Final"
val dataJpa = "3.2.3"
val slf4jV = "2.0.2"

dependencies {
    api("cz.jirutka.rsql:rsql-parser:2.1.0")
    implementation("org.hibernate:hibernate-core:$hibernate")
    api("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.springframework.data:spring-data-jpa:$dataJpa")
    implementation("org.slf4j:slf4j-ext:$slf4jV")
}

java {
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>() {
    kotlinOptions{
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
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
            repositories {
                maven {
                    val repositoryUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotRepositoryUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotRepositoryUrl else repositoryUrl)
                    credentials {
                        val ossUsername: String? by project
                        val ossPassword: String? by project
                        username = ossUsername
                        password = ossPassword
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications.getByName("mavenJava"))
}