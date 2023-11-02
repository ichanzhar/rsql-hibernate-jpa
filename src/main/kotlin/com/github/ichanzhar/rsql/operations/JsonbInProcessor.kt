package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.CollectionJoin
import jakarta.persistence.criteria.ListJoin
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.SetJoin
import org.hibernate.query.criteria.JpaRoot
import java.util.*

class JsonbInProcessor(params: Params) : AbstractProcessor(params) {

    override fun process(): Predicate {
        val args = (params.args[0] as String).split("|")
        if (args.size != 2) {
            throw IllegalArgumentException("=jsonbin= operator expects 2 parts json path and value my.path|myvalue1,myvalue2)")
        }
        val stringJoiner = StringJoiner(" || ")
        args[1].split(",").forEach { stringJoiner.add("@ == \"$it\"") }
        val path = params.builder.literal("$.${args[0]} ? (" + stringJoiner.toString() + ")")
        when {
            isRootJoin() -> return params.builder.isTrue(
                params.builder.function(
                    "jsonb_filter",
                    Boolean::class.java,
                    (params.root as JpaRoot<*>).join<Any, Any>(params.property),
                    path
                )
            )

            isCollectionJoin() -> {
                return params.builder.isTrue(
                    params.builder.function(
                        "jsonb_filter",
                        Boolean::class.java,
                        (params.root as CollectionJoin<*, *>).join<Any, Any>(params.property),
                        path
                    )
                )
            }

            isSetJoin() -> return params.builder.isTrue(
                params.builder.function(
                    "jsonb_filter",
                    Boolean::class.java,
                    (params.root as SetJoin<*, *>).join<Any, Any>(params.property),
                    path
                )
            )
            isListJoin() -> return params.builder.isTrue(
                params.builder.function(
                    "jsonb_filter",
                    Boolean::class.java,
                    (params.root as ListJoin<*, *>).join<Any, Any>(params.property),
                    path
                )
            )
            else -> {
                return params.builder.isTrue(
                    params.builder.function(
                        "jsonb_filter",
                        Boolean::class.java,
                        params.root.get<Any>(params.property),
                        path
                    )
                )
            }
        }
    }
}
