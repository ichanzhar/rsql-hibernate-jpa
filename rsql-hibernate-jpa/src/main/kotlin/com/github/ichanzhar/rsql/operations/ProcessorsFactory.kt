package com.github.ichanzhar.rsql.operations

import com.github.ichanzhar.rsql.utils.RsqlOperationsRegistry
import cz.jirutka.rsql.parser.ast.ComparisonOperator

object ProcessorsFactory {

	/**
	 * Processor for your visitor or Specification
	 * @see com.github.ichanzhar.rsql.JpaRsqlSpecification
	 * @see com.github.ichanzhar.rsql.JpaRsqlVisitor
	 */
	fun getProcessor(operator: ComparisonOperator, params: Params): Processor {
		return RsqlOperationsRegistry.operationProcessors[operator]!!(params)
	}

}