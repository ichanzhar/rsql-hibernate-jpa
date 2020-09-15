package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.internal.path.RootImpl
import org.hibernate.query.criteria.internal.path.SetAttributeJoin
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate

class EqualProcessor(params: Params) : AbstractProcessor(params) {
    override fun process(): Predicate {
        if (isRootJoin()) {
            return if (isLikeExpression()) {
                params.builder.like(
                    (params.root as RootImpl).join<Any, String>(params.property)  as Expression<String>,
                    getFormattedLikePattern()
                )
            } else params.builder.equal(
                (params.root as RootImpl).join<Any, Any>(params.property),
                params.argument
            )
        } else if (isSingularJoin()) {
            return if (isLikeExpression()) {
                params.builder.like(
                    (params.root as SingularAttributeJoin<*, *>).join<Any, String>(params.property) as Expression<String>,
                    getFormattedLikePattern()
                )
            } else params.builder.equal(
                (params.root as SingularAttributeJoin<*, *>).join<Any, Any>(params.property),
                params.argument
            )
        } else if (isSetJoin()) {
            return if (isLikeExpression()) {
                params.builder.like(
                    (params.root as SetAttributeJoin<*, *>).join<Any, String>(params.property) as Expression<String>,
                    getFormattedLikePattern()
                )
            } else params.builder.equal(
                (params.root as SetAttributeJoin<*, *>).join<Any, Any>(params.property),
                params.argument
            )
        } else if (isLikeExpression()) {
            return params.builder.like(
                params.root.get(params.property),
                getFormattedLikePattern()
            )
        } else if (params.argument == null) {
            return params.builder.isNull(params.root.get<Any>(params.property))
        } else {
            return params.builder.equal(params.root.get<Any>(params.property), params.argument)
        }
    }
}