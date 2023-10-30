package com.github.ichanzhar.rsql.operations


import org.hibernate.query.criteria.*
import jakarta.persistence.criteria.*

class NotInProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		return when {
			isRootJoin() -> {
				params.builder.not((params.root as JpaRoot<*>).join<Any, Any>(params.property).`in`(params.args))
			}
			isListJoin() -> {
				params.builder.not((params.root as ListJoin<*, *>).join<Any, Any>(params.property).`in`(params.args))
			}
			isSetJoin() -> {
				params.builder.not((params.root as SetJoin<*, *>).join<Any, Any>(params.property).`in`(params.args))
			}
			isCollectionJoin() -> {
				params.builder.not((params.root as CollectionJoin<*, *>).join<Any, Any>(params.property).`in`(params.args))
			}
			else -> {
				params.builder.not(params.root.get<Any>(params.property).`in`(params.args))
			}
		}
	}

}