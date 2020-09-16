package com.github.ichanzhar.rsql.operations

import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Path


data class Params(
    var root: Path<*>,
    var builder: CriteriaBuilder,
    var property: String,
    var globalProperty: String,
    var args: List<Any>,
    var argument: Any?,
    var javaType: Class<out Any>?
)