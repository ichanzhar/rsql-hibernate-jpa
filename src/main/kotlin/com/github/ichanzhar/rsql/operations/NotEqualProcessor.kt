package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.*
import org.hibernate.query.criteria.JpaRoot
class NotEqualProcessor(params: Params) : AbstractProcessor(params) {

    override fun process(): Predicate {
        when {
            isRootJoin() -> {
                return if (isLikeExpression()) {
                    params.builder.notLike(
                        (params.root as JpaRoot).join<Any, String>(params.property)  as Expression<String>,
                        getFormattedLikePattern()
                    )
                } else params.builder.notEqual((params.root as JpaRoot).join<Any, Any>(params.property), params.argument)
            }
            isCollectionJoin() -> {
                return if (isLikeExpression()) {
                    params.builder.notLike(
                        (params.root as CollectionJoin<*, *>).join<Any, String>(params.property) as Expression<String>,
                        getFormattedLikePattern()
                    )
                } else params.builder.notEqual(
                    (params.root as CollectionJoin<*, *>).join<Any, Any>(params.property), params.argument)
            }
            isSetJoin() -> {
                return if (isLikeExpression()) {
                    params.builder.notLike(
                        (params.root as SetJoin<*, *>).join<Any, String>(params.property) as Expression<String>,
                        getFormattedLikePattern()
                    )
                } else params.builder.notEqual((params.root as SetJoin<*, *>).join<Any, Any>(params.property), params.argument)
            }
            isListJoin() -> {
                return if (isLikeExpression()) {
                    params.builder.notLike(
                        (params.root as ListJoin<*, *>).join<Any, String>(params.property) as Expression<String>,
                        getFormattedLikePattern()
                    )
                } else params.builder.notEqual((params.root as ListJoin<*, *>).join<Any, Any>(params.property), params.argument)
            }
            isLikeExpression() -> {
                return params.builder.notLike(params.root.get(params.property), getFormattedLikePattern())
            }
            params.argument == null -> {
                return params.builder.isNull(params.root.get<Any>(params.property))
            }
            else -> {
                return params.builder.notEqual(params.root.get<Any>(params.property), params.argument)
            }
        }
    }
}