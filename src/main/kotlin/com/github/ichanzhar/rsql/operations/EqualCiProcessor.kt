package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.*
import jakarta.persistence.criteria.*

class EqualCiProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		when {
			isRootJoin() -> {
				return params.builder.like(
					params.builder.lower((params.root as JpaRoot).join<Any, String>(params.property) as Expression<String>),
					getFormattedLikePattern().lowercase()
				)
			}
			isListJoin() -> {
				return params.builder.like(
					params.builder.lower((params.root as ListJoin<*, *>).join<Any, String>(params.property) as Expression<String>),
					getFormattedLikePattern().lowercase()
				)
			}
			isCollectionJoin() -> {
				return params.builder.like(
					params.builder.lower((params.root as CollectionJoin<*, *>).join<Any, String>(params.property) as Expression<String>),
					getFormattedLikePattern().lowercase()
				)
			}
			isSetJoin() -> {
				return params.builder.like(
					params.builder.lower((params.root as SetJoin<*, *>).join<Any, String>(params.property) as Expression<String>),
					getFormattedLikePattern().lowercase()
				)
			}
			params.argument == null -> {
				return params.builder.isNull(params.root.get<Any>(params.property))
			}
			else -> {
				return params.builder.like(
					params.builder.lower(params.root.get(params.property)),
					getFormattedLikePattern().lowercase()
				)
			}
		}
	}
}