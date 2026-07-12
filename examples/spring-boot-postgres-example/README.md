# spring-boot-postgres-example

A Spring Boot + PostgreSQL app demonstrating
[`com.github.ichanzhar:rsql-hibernate-jpa`](https://github.com/ichanzhar/rsql-hibernate-jpa)
against a deliberately varied JPA domain: to-one and to-many associations, element collections
(at the root and nested below an association), a many-to-many join, an embedded value object,
and the library's Postgres-only `=jsonbeq=` JSON operator.

This is a **standalone Gradle project** with its own wrapper and `settings.gradle.kts` — it is
not part of the parent repo's root build or CI, and can be copied out of this repository and run
on its own. It depends on the library as a normal Maven Central artifact
(`com.github.ichanzhar:rsql-hibernate-jpa:0.21`), not as a sibling module.

## Domain

- `Author` — name, email.
- `Book` — title, isbn, publicationYear, jsonb `metadata`, `@ManyToOne` `author`,
  `@Embedded` `dimensions` (width/height/weight), `@ElementCollection` `tags` (`Set<String>`),
  `@OneToMany` `reviews` (`Set<Review>`), `@OneToMany` `chapters` (`List<Chapter>`),
  `@ManyToMany` `categories` (`Set<Category>`).
- `Review` — rating, comment, `@ElementCollection` `labels` (`Set<String>`) — one hop *below*
  the `reviews` association.
- `Chapter` — sequence, title.
- `Category` — name.

All queries go through `GET /books?query=<rsql>`, resolved via `RsqlParserFactory` +
`JpaRsqlVisitor<Book>(distinct = true)` (`distinct` avoids duplicate rows fanned out by the
collection joins below).

## Query catalogue

| Query | Domain feature | Library code path exercised |
|---|---|---|
| `author.name==*Tolkien*` | `@ManyToOne` association, dot-notation join, wildcard LIKE | `JpaRsqlSpecification` join-path handling |
| `publicationYear=gt=1950` | plain scalar field | `GtProcessor` |
| `metadata=jsonbeq=genre\|scifi` | Postgres `jsonb` column | `JsonbEqualProcessor` (Postgres-only) |
| `tags==classic` | `@ElementCollection` directly on the root entity, **no dot-notation** | `AbstractProcessor.isRootJoin()` |
| `reviews.rating==5` | `@OneToMany Set<Review>`, ordinary nested field | standard join path |
| `reviews.labels==urgent` | `@ElementCollection` reached **through** a `@OneToMany` join | `AbstractProcessor.isSetJoin()` — join, then a further collection-valued attribute |
| `chapters.title==Prologue` | `@OneToMany List<Chapter>` (as opposed to a `Set`) | join path over a `List`-typed association |
| `categories.name==Fantasy` | `@ManyToMany` join | join path over a many-to-many association |
| `dimensions.weightGrams=gt=400` | `@Embedded` value object | `JpaRsqlSpecification`'s embedded-attribute handling (same branch as associations) |
| `author.name==*Tolkien*;reviews.labels==editorial` | combining a to-one join and a collection filter | `AND` combination (`GenericRsqlSpecBuilder`) |

See `BookControllerIntegrationTest` for all of these run against a real Postgres instance.

## Requirements

- JDK 17+
- Docker (required to run the integration test, which starts a real Postgres via Testcontainers)

## Run the tests

```bash
./gradlew test
```

## Run the app

Start a local Postgres:

```bash
docker run --rm -p 5432:5432 \
  -e POSTGRES_DB=rsql_example -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine
```

Run with the `demo` profile to seed a few sample rows:

```bash
SPRING_PROFILES_ACTIVE=demo ./gradlew bootRun
```

Then try some RSQL queries:

```bash
curl 'http://localhost:8080/books?query=author.name==*Tolkien*'
curl 'http://localhost:8080/books?query=publicationYear=gt=1950'
curl -G 'http://localhost:8080/books' --data-urlencode 'query=metadata=jsonbeq=genre|scifi'
curl -G 'http://localhost:8080/books' --data-urlencode 'query=reviews.labels==urgent'
curl -G 'http://localhost:8080/books' --data-urlencode 'query=dimensions.weightGrams=gt=400'
```
