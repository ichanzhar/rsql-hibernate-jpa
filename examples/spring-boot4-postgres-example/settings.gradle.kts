rootProject.name = "spring-boot4-postgres-example"

includeBuild("../..") {
    dependencySubstitution {
        substitute(module("com.github.ichanzhar:rsql-hibernate-jpa")).using(project(":rsql-hibernate-jpa"))
    }
}
