package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.Predicate

class IsNullProcessor (params: Params) : AbstractProcessor(params) {

    override fun process(): Predicate {
        return if ((params.argument as String).toBoolean()) {
            params.builder.isNull(params.root.get<String>(params.property))
        } else {
            params.builder.isNotNull(params.root.get<String>(params.property))
        }
    }

}