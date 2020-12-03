package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.internal.path.RootImpl
import org.hibernate.query.criteria.internal.path.SetAttributeJoin
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate

class EqualCiProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		if (isRootJoin()) {
			return params.builder.like(
                    params.builder.lower((params.root as RootImpl).join<Any, String>(params.property) as Expression<String>),
                    getFormattedLikePattern().toLowerCase()
            )
		} else if (isSingularJoin()) {
			return params.builder.like(
                    params.builder.lower((params.root as SingularAttributeJoin<*, *>).join<Any, String>(params.property) as Expression<String>),
                    getFormattedLikePattern().toLowerCase()
            )
		} else if (isSetJoin()) {
			return params.builder.like(
                    params.builder.lower((params.root as SetAttributeJoin<*, *>).join<Any, String>(params.property) as Expression<String>),
                    getFormattedLikePattern().toLowerCase())
		} else if (params.argument == null) {
			return params.builder.isNull(params.root.get<Any>(params.property))
		} else {
			return params.builder.like(
                    params.builder.lower(params.root.get(params.property)),
                    getFormattedLikePattern().toLowerCase()
            )
		}
	}
}