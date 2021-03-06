package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.Predicate

class GtProcessor(params: Params) : AbstractProcessor(params) {
	@Suppress("UNCHECKED_CAST")
	override fun process(): Predicate {
		return params.builder.greaterThan(params.root.get(params.property), params.argument as Comparable<Any>)
	}
}