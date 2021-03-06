package com.github.ichanzhar.rsql.operations

import com.github.ichanzhar.rsql.RsqlOperation
import cz.jirutka.rsql.parser.ast.ComparisonOperator

class ProcessorsFactory {
	companion object {
		fun getProcessor(operator: ComparisonOperator, params: Params): Processor {
			return when (RsqlOperation.getSimpleOperator(operator)) {
				RsqlOperation.EQUAL -> EqualProcessor(params)
				RsqlOperation.NOT_EQUAL -> NotEqualProcessor(params)
				RsqlOperation.GREATER_THAN -> GtProcessor(params)
				RsqlOperation.GREATER_THAN_OR_EQUAL -> GteProcessor(params)
				RsqlOperation.LESS_THAN -> LtProcessor(params)
				RsqlOperation.LESS_THAN_OR_EQUAL -> LteProcessor(params)
				RsqlOperation.IN -> InProcessor(params)
				RsqlOperation.NOT_IN -> NotInProcessor(params)
				RsqlOperation.IS_NULL -> IsNullProcessor(params)
				RsqlOperation.EQUAL_CI -> EqualCiProcessor(params)
			}
		}
	}
}