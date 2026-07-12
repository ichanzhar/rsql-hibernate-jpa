# rsql-hibernate-jpa
[![Sonatype Nexus (Releases)](https://img.shields.io/maven-central/v/com.github.ichanzhar/rsql-hibernate-jpa?label=Release)](https://oss.sonatype.org/#nexus-search;gav~com.github.ichanzhar~rsql-hibernate-jpa)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa?ref=badge_shield)


* RSQL implementation for Hibernate/Spring data with join tables support (JDK 21, Spring Boot 4.x)

## Library versions

| Library version | Spring Boot |
|---|---|
| 0.1x | 2.x |
| 0.2x | 3.x |
| 4.x | 4.x |

## Repository layout

This repository is a monorepo:
* [`rsql-hibernate-jpa/`](rsql-hibernate-jpa) — the library itself, published to Maven Central.
* [`examples/`](examples) — example projects demonstrating library usage. Each example is a
  fully independent, standalone Gradle build (own wrapper, own `settings.gradle.kts`) — not part
  of this repo's root build or CI. [`examples/spring-boot-postgres-example`](examples/spring-boot-postgres-example)
  is a Spring Boot 3.5 + Postgres demo depending on the released `0.21` artifact;
  [`examples/spring-boot4-postgres-example`](examples/spring-boot4-postgres-example) is the same
  demo on Spring Boot 4.1, consuming the library source via a Gradle composite build until
  `4.0.0` is published to Maven Central. Both cover core RSQL operators, join-path filtering,
  and the Postgres-only `=jsonbeq=` operator.

## Dependencies:
* Hibernate Core 7.4.1.Final
* Spring Data JPA 4.1.0
* RSQL Parser - [rsql-parser](https://github.com/jirutka/rsql-parser)

## License

* This project is licensed under [MIT license](http://opensource.org/licenses/MIT)


[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fichanzhar%2Frsql-hibernate-jpa?ref=badge_large)