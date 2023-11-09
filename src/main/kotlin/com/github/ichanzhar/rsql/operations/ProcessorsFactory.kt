package com.github.ichanzhar.rsql.operations

import com.github.ichanzhar.rsql.utils.OperationRegistryFactory
import cz.jirutka.rsql.parser.ast.ComparisonOperator

object ProcessorsFactory {
	@Deprecated("We move this realization to other class",
		ReplaceWith("OperationRegistryFactory.getProcessor()","com.github.ichanzhar.rsql.utils.OperationRegistryFactory"))
	fun getProcessor(operator: ComparisonOperator, params: Params): Processor {
		return OperationRegistryFactory.getProcessor(operator, params)
	}

}