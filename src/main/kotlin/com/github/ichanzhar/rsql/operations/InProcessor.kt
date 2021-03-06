package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.internal.path.RootImpl
import org.hibernate.query.criteria.internal.path.SetAttributeJoin
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin
import javax.persistence.criteria.Predicate

class InProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		return when {
			isRootJoin() -> {
				(params.root as RootImpl<*>).join<Any, Any>(params.property).`in`(params.args)
			}
			isSingularJoin() -> {
				(params.root as SingularAttributeJoin<*, *>).join<Any, Any>(params.property).`in`(params.args)
			}
			isSetJoin() -> {
				(params.root as SetAttributeJoin<*, *>).join<Any, Any>(params.property).`in`(params.args)
			}
			else -> {
				params.root.get<Any>(params.property).`in`(params.args)
			}
		}
	}
}