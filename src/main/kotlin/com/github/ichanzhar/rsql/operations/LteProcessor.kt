package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.Predicate

class LteProcessor(params: Params) : AbstractProcessor(params) {
	@Suppress("UNCHECKED_CAST")
	override fun process(): Predicate {
		return params.builder.lessThanOrEqualTo(params.root.get(params.property), params.argument as Comparable<Any>)
	}
}