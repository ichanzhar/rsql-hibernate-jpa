package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.CollectionJoin
import jakarta.persistence.criteria.ListJoin
import jakarta.persistence.criteria.SetJoin
import org.hibernate.query.criteria.JpaRoot

class JsonbEqualProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		val args = (params.args[0] as String).split("|")
		if (args.size != 2) {
			throw IllegalArgumentException("=jsonbeq= operator expects 2 parts json path and value my.path|myvalue")
		}
		val path = args[0].split(".").map { params.builder.literal(it) }.toTypedArray()
		val argument = args[1]
		when {
			isRootJoin() -> {
				return params.builder.equal(
					params.builder.function(
						"jsonb_extract_path_text",
						String::class.java,
						(params.root as JpaRoot<*>).join<Any, Any>(params.property),
						*path
					),
					argument
				)
			}
			isCollectionJoin() -> {
				return params.builder.equal(
					params.builder.function(
						"jsonb_extract_path_text",
						String::class.java,
						(params.root as CollectionJoin<*, *>).join<Any, Any>(params.property),
						*path
					),
					argument
				)
			}
			isSetJoin() -> {
				return params.builder.equal(
					(params.root as SetJoin<*, *>).join<Any, Any>(params.property), params.argument
				)
			}
			isListJoin() -> {
				return params.builder.equal(
					(params.root as ListJoin<*, *>).join<Any, Any>(params.property), params.argument
				)
			}
			else -> {
				return params.builder.equal(
					params.builder.function(
						"jsonb_extract_path_text",
						String::class.java,
						params.root.get<Any>(params.property),
						*path
					),
					argument
				)
			}
		}
	}
}