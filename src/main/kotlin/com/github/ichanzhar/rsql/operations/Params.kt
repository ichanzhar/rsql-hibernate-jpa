package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.*


data class Params(
    var root: Path<*>,
    var builder: CriteriaBuilder,
    var property: String,
    var globalProperty: String,
    var args: List<Any>,
    var argument: Any?
)