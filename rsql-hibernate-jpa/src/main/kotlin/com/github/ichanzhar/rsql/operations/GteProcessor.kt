package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.*

class GteProcessor(params: Params) : AbstractProcessor(params) {
	@Suppress("UNCHECKED_CAST")
	override fun process(): Predicate {
		return params.builder.greaterThanOrEqualTo(params.root.get(params.property), params.argument as Comparable<Any>)
	}
}