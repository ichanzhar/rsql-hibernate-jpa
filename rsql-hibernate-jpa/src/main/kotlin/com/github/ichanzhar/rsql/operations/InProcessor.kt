package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.*
import jakarta.persistence.criteria.*

class InProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		return when {
			isRootJoin() -> {
				(params.root as JpaRoot<*>).join<Any, Any>(params.property).`in`(params.args)
			}
			isListJoin() -> {
				(params.root as ListJoin<*, *>).join<Any, Any>(params.property).`in`(params.args)
			}
			isSetJoin() -> {
				(params.root as SetJoin<*, *>).join<Any, Any>(params.property).`in`(params.args)
			}
			isCollectionJoin() -> {
				(params.root as CollectionJoin<*, *>).join<Any, Any>(params.property).`in`(params.args)
			}
			else -> {
				params.root.get<Any>(params.property).`in`(params.args)
			}
		}
	}
}