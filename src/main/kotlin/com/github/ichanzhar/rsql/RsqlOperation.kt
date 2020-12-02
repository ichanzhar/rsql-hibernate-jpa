package com.github.ichanzhar.rsql

import cz.jirutka.rsql.parser.ast.ComparisonOperator
import cz.jirutka.rsql.parser.ast.RSQLOperators
import java.util.*

enum class RsqlOperation(val operator: ComparisonOperator) {
	EQUAL(RSQLOperators.EQUAL),
	NOT_EQUAL(RSQLOperators.NOT_EQUAL),
	GREATER_THAN(RSQLOperators.GREATER_THAN),
	GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
	LESS_THAN(RSQLOperators.LESS_THAN),
	LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
	IN(RSQLOperators.IN),
	NOT_IN(RSQLOperators.NOT_IN),
	IS_NULL(ComparisonOperator("=isNull="));

	companion object {
		fun getSimpleOperator(operator: ComparisonOperator): RsqlOperation {
			return Arrays.stream(values())
				.filter { operation: RsqlOperation -> operation.operator === operator }
				.findAny().orElse(null)
		}
	}
}