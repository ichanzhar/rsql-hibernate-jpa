package com.github.ichanzhar.rsql.exception

import cz.jirutka.rsql.parser.ast.ComparisonOperator

class UnknownOperatorException(operator: ComparisonOperator?) : RuntimeException("unknown operator $operator")