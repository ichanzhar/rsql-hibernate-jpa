package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.Predicate

class LtProcessor(params: Params) : AbstractProcessor(params) {
    @Suppress("UNCHECKED_CAST")
    override fun process(): Predicate {
        return params.builder.lessThanOrEqualTo(params.root.get(params.property), params.argument as Comparable<Any>)
    }
}