package com.github.ichanzhar.rsql.operations

import java.util.*
import javax.persistence.criteria.Predicate

class LtProcessor(params: Params) : AbstractProcessor(params) {
    override fun process(): Predicate {
        return if (isDate()) params.builder.lessThan(params.root.get(params.property), params.argument as Date?)
        else params.builder.lessThan(params.root.get(params.property), params.argument.toString())
    }
}