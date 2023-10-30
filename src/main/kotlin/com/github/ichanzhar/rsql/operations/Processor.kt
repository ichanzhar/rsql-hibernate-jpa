package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.Predicate
interface Processor {
	fun process(): Predicate
}