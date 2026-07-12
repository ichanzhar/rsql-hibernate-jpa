package com.github.ichanzhar.rsql.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootPostgresExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringBootPostgresExampleApplication>(*args)
}
