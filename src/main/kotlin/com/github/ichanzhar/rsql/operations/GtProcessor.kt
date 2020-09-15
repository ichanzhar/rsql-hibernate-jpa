package com.github.ichanzhar.rsql.operations

import java.util.*
import javax.persistence.criteria.Predicate

class GtProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		return if (isDate()) params.builder.greaterThan(params.root.get(params.property), params.argument as Date)
		else params.builder.greaterThan(params.root.get(params.property), params.argument.toString())
	}
}