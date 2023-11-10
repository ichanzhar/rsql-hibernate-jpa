package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.ParserContext
import com.github.ichanzhar.rsql.RsqlOperation
import com.github.ichanzhar.rsql.operations.*
import cz.jirutka.rsql.parser.ast.ComparisonOperator

// Equal of Function<Params, Processor> in Java
typealias ProcessorParamsBuilder = (Params) -> Processor

/**
 * @param operationProcessors Map with operator and Processor. Initialize by default operators from [ParserContext.COMMON]
 *
 * @param operations list of available operators.
 * Old: RsqlOperation.values().filter { activeContexts.contains(it.context) }.map { it.operator }.toMutableSet()
 */
object RsqlOperationsRegistry {
    val operationProcessors: MutableMap<ComparisonOperator, ProcessorParamsBuilder> = mutableMapOf(
        RsqlOperation.EQUAL.operator to { EqualProcessor(it) },
        RsqlOperation.NOT_EQUAL.operator to { NotEqualProcessor(it) },
        RsqlOperation.GREATER_THAN.operator to { GtProcessor(it) },
        RsqlOperation.GREATER_THAN_OR_EQUAL.operator to { GteProcessor(it) },
        RsqlOperation.LESS_THAN.operator to { LtProcessor(it) },
        RsqlOperation.LESS_THAN_OR_EQUAL.operator to { LteProcessor(it) },
        RsqlOperation.IN.operator to { InProcessor(it) },
        RsqlOperation.NOT_IN.operator to { NotInProcessor(it) },
        RsqlOperation.IS_NULL.operator to { IsNullProcessor(it) },
        RsqlOperation.EQUAL_CI.operator to { EqualCiProcessor(it) },
        RsqlOperation.IS_EMPTY.operator to { IsEmptyProcessor(it) },
    )
    val operations: Set<ComparisonOperator>
        get() = operationProcessors.keys

    /**
     * @param operator Your custom [ComparisonOperator]
     * @sample RsqlOperation.JSON_EQ.operator
     *
     * @param processor This is lambda function for initialize your processor in [GenericRsqlSpecBuilder]
     * @see com.github.ichanzhar.rsql.GenericRsqlSpecBuilder
     * @sample addDefaultPostgresOperation
     */
    fun registerOperation(operator: ComparisonOperator, processor: ProcessorParamsBuilder) {
        operationProcessors[operator] = processor
    }

    /**
     * Add to [operationProcessors] operation from [RsqlOperation] with context [ParserContext.POSTGRESQL]
     */
    fun initDefaultPostgresOperation() {
        registerOperation(RsqlOperation.JSON_EQ.operator) { JsonEqualProcessor(it) }
        registerOperation(RsqlOperation.JSONB_EQ.operator) { JsonbEqualProcessor(it) }
    }

}
