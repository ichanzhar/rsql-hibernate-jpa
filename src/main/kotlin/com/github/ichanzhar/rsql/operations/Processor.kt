package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.Predicate

interface Processor {
	fun process(): Predicate
}