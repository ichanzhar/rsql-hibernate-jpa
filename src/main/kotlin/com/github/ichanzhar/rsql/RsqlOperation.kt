package com.github.ichanzhar.rsql

import cz.jirutka.rsql.parser.ast.ComparisonOperator
import cz.jirutka.rsql.parser.ast.RSQLOperators
import java.util.*

enum class RsqlOperation(val operator: ComparisonOperator, val context: ParserContext) {
	EQUAL(RSQLOperators.EQUAL, ParserContext.COMMON),
	NOT_EQUAL(RSQLOperators.NOT_EQUAL, ParserContext.COMMON),
	GREATER_THAN(RSQLOperators.GREATER_THAN, ParserContext.COMMON),
	GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL, ParserContext.COMMON),
	LESS_THAN(RSQLOperators.LESS_THAN, ParserContext.COMMON),
	LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL, ParserContext.COMMON),
	IN(RSQLOperators.IN, ParserContext.COMMON),
	NOT_IN(RSQLOperators.NOT_IN, ParserContext.COMMON),
	IS_NULL(ComparisonOperator("=isNull="), ParserContext.COMMON),
	EQUAL_CI(ComparisonOperator("=eqci="), ParserContext.COMMON),/*case insensitive equility operator*/
	IS_EMPTY(ComparisonOperator("=isEmpty="), ParserContext.COMMON),
	JSONB_EQ(ComparisonOperator("=jsonbeq="), ParserContext.POSTGRESQL),
	JSON_EQ(ComparisonOperator("=jsoneq="), ParserContext.POSTGRESQL);

	companion object {
		fun getSimpleOperator(operator: ComparisonOperator): RsqlOperation {
			return Arrays.stream(values())
				.filter { operation: RsqlOperation -> operation.operator === operator }
				.findAny().orElse(null)
		}
	}
}