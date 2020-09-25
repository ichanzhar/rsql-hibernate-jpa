package com.github.ichanzhar.rsql.operations

import java.util.*
import javax.persistence.criteria.Predicate

class GteProcessor(params: Params) : AbstractProcessor(params) {
	@Suppress("UNCHECKED_CAST")
	override fun process(): Predicate {
		return params.builder.lessThanOrEqualTo(params.root.get(params.property), params.argument as Comparable<Any>)
	}
}