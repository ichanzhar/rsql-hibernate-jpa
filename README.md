# rsql-hibernate-jpa
[![Sonatype Nexus (Releases)](https://img.shields.io/maven-central/v/com.github.ichanzhar/rsql-hibernate-jpa?label=Release)](https://oss.sonatype.org/#nexus-search;gav~com.github.ichanzhar~rsql-hibernate-jpa)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa?ref=badge_shield)


* RSQL implementation for Hibernate/Spring data with join tables support(JDK 17, Spring Boot 3.0+)

## Repository layout

This repository is a monorepo:
* [`rsql-hibernate-jpa/`](rsql-hibernate-jpa) — the library itself, published to Maven Central.
* [`examples/`](examples) — example projects demonstrating library usage. Each example is a
  fully independent, standalone Gradle build (own wrapper, own `settings.gradle.kts`) depending
  on the library as a normal external Maven dependency — not part of this repo's root build or
  CI. See [`examples/spring-boot-postgres-example`](examples/spring-boot-postgres-example) for a
  Spring Boot + Postgres demo covering core RSQL operators, join-path filtering, and the
  Postgres-only `=jsonbeq=` operator.

## Dependencies:
* Hibernate Core 7.1.0.Final
* Spring Data JPA 3.5.3
* RSQL Parser - [rsql-parser](https://github.com/jirutka/rsql-parser)

## License

* This project is licensed under [MIT license](http://opensource.org/licenses/MIT)


[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa?ref=badge_large)