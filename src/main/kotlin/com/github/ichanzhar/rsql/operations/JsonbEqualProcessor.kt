package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.internal.path.RootImpl
import org.hibernate.query.criteria.internal.path.SetAttributeJoin
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate

class JsonbEqualProcessor(params: Params) : AbstractProcessor(params) {
	override fun process(): Predicate {
		val args = (params.args[0] as String).split("|")
		if (args.size != 2) {
			throw IllegalArgumentException("=jsonbeq= operator expects 2 parts json path and value my.path|myvalue")
		}
		val path = args[0].split(".").map { params.builder.literal(it) }.toTypedArray()
		val argument = args[1]
		if (isRootJoin()) {
			return params.builder.equal(
				params.builder.function(
					"jsonb_extract_path_text",
					String::class.java,
					(params.root as RootImpl).join<Any, Any>(params.property),
					*path
				),
				argument
			)
		} else if (isSingularJoin()) {
			return params.builder.equal(
				params.builder.function(
					"jsonb_extract_path_text",
					String::class.java,
					(params.root as SingularAttributeJoin<*, *>).join<Any, Any>(params.property),
					*path
				),
				argument
			)
		} else if (isSetJoin()) {
			return params.builder.equal(
				(params.root as SetAttributeJoin<*, *>).join<Any, Any>(params.property), params.argument)
		} else {
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