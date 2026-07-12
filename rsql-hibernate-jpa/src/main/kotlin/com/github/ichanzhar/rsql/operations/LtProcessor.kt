package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.Predicate

class LtProcessor(params: Params) : AbstractProcessor(params) {
    @Suppress("UNCHECKED_CAST")
    override fun process(): Predicate {
        return params.builder.lessThan(params.root.get(params.property), params.argument as Comparable<Any>)
    }
}