package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.Predicate

class IsEmptyProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		return if ((params.argument as String).toBoolean()) {
			params.builder.isEmpty(params.root.get<Collection<Any>>(params.property))
		} else {
			params.builder.isNotEmpty(params.root.get<Collection<Any>>(params.property))
		}
	}
}