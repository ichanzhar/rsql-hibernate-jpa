package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.RsqlOperation
import com.github.ichanzhar.rsql.operations.Processor
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import io.mockk.mockk
import jakarta.persistence.criteria.Predicate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RsqlOperationsRegistryTest {

    @BeforeEach
    fun setUp() {
        // Reset the registry to default state by re-initializing
        // Note: Since this is an object, we need to be careful with state between tests
    }

    @Test
    fun `should contain default EQUAL operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.EQUAL.operator))
    }

    @Test
    fun `should contain default NOT_EQUAL operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.NOT_EQUAL.operator))
    }

    @Test
    fun `should contain default GREATER_THAN operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.GREATER_THAN.operator))
    }

    @Test
    fun `should contain default GREATER_THAN_OR_EQUAL operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.GREATER_THAN_OR_EQUAL.operator))
    }

    @Test
    fun `should contain default LESS_THAN operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.LESS_THAN.operator))
    }

    @Test
    fun `should contain default LESS_THAN_OR_EQUAL operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.LESS_THAN_OR_EQUAL.operator))
    }

    @Test
    fun `should contain default IN operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.IN.operator))
    }

    @Test
    fun `should contain default NOT_IN operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.NOT_IN.operator))
    }

    @Test
    fun `should contain default IS_NULL operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.IS_NULL.operator))
    }

    @Test
    fun `should contain default EQUAL_CI operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.EQUAL_CI.operator))
    }

    @Test
    fun `should contain default IS_EMPTY operator`() {
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.IS_EMPTY.operator))
    }

    @Test
    fun `should register custom operation`() {
        val customOperator = ComparisonOperator("=customOp=")
        val mockPredicate: Predicate = mockk(relaxed = true)
        val customProcessor: ProcessorParamsBuilder = { _ ->
            object : Processor {
                override fun process(): Predicate = mockPredicate
            }
        }

        RsqlOperationsRegistry.registerOperation(customOperator, customProcessor)

        assertTrue(RsqlOperationsRegistry.operations.contains(customOperator))
        assertNotNull(RsqlOperationsRegistry.operationProcessors[customOperator])
    }

    @Test
    fun `should initialize PostgreSQL operations`() {
        RsqlOperationsRegistry.initDefaultPostgresOperation()

        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.JSON_EQ.operator))
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.JSONB_EQ.operator))
    }

    @Test
    fun `should return correct number of default operations`() {
        // Default operations: EQUAL, NOT_EQUAL, GT, GTE, LT, LTE, IN, NOT_IN, IS_NULL, EQUAL_CI, IS_EMPTY = 11
        assertTrue(RsqlOperationsRegistry.operations.size >= 11)
    }

    @Test
    fun `operationProcessors should return processor builders for all operations`() {
        RsqlOperationsRegistry.operations.forEach { operator ->
            val processorBuilder = RsqlOperationsRegistry.operationProcessors[operator]
            assertNotNull(processorBuilder, "Processor builder should exist for operator: ${operator.symbol}")
        }
    }

    @Test
    fun `should be able to override existing operator processor`() {
        val mockPredicate: Predicate = mockk(relaxed = true)
        val customProcessor: ProcessorParamsBuilder = { _ ->
            object : Processor {
                override fun process(): Predicate = mockPredicate
            }
        }

        val originalProcessor = RsqlOperationsRegistry.operationProcessors[RsqlOperation.EQUAL.operator]
        RsqlOperationsRegistry.registerOperation(RsqlOperation.EQUAL.operator, customProcessor)

        val newProcessor = RsqlOperationsRegistry.operationProcessors[RsqlOperation.EQUAL.operator]
        assertNotSame(originalProcessor, newProcessor)
    }

    @Test
    fun `operations set should be backed by operationProcessors keys`() {
        assertEquals(RsqlOperationsRegistry.operationProcessors.keys, RsqlOperationsRegistry.operations)
    }
}
