package com.github.ichanzhar.rsql.operations

import java.util.*
import javax.persistence.criteria.Predicate

class LteProcessor(params: Params) : AbstractProcessor(params) {
    override fun process(): Predicate {
        return if (isDate()) params.builder.lessThanOrEqualTo(params.root.get(params.property), params.argument as Date?)
        else params.builder.lessThanOrEqualTo(params.root.get(params.property), params.argument.toString())
    }
}