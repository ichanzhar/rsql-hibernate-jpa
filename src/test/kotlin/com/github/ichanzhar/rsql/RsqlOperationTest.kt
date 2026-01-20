package com.github.ichanzhar.rsql

import cz.jirutka.rsql.parser.ast.ComparisonOperator
import cz.jirutka.rsql.parser.ast.RSQLOperators
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class RsqlOperationTest {

    @Test
    fun `should have correct operator for EQUAL`() {
        assertEquals(RSQLOperators.EQUAL, RsqlOperation.EQUAL.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.EQUAL.context)
    }

    @Test
    fun `should have correct operator for NOT_EQUAL`() {
        assertEquals(RSQLOperators.NOT_EQUAL, RsqlOperation.NOT_EQUAL.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.NOT_EQUAL.context)
    }

    @Test
    fun `should have correct operator for GREATER_THAN`() {
        assertEquals(RSQLOperators.GREATER_THAN, RsqlOperation.GREATER_THAN.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.GREATER_THAN.context)
    }

    @Test
    fun `should have correct operator for GREATER_THAN_OR_EQUAL`() {
        assertEquals(RSQLOperators.GREATER_THAN_OR_EQUAL, RsqlOperation.GREATER_THAN_OR_EQUAL.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.GREATER_THAN_OR_EQUAL.context)
    }

    @Test
    fun `should have correct operator for LESS_THAN`() {
        assertEquals(RSQLOperators.LESS_THAN, RsqlOperation.LESS_THAN.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.LESS_THAN.context)
    }

    @Test
    fun `should have correct operator for LESS_THAN_OR_EQUAL`() {
        assertEquals(RSQLOperators.LESS_THAN_OR_EQUAL, RsqlOperation.LESS_THAN_OR_EQUAL.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.LESS_THAN_OR_EQUAL.context)
    }

    @Test
    fun `should have correct operator for IN`() {
        assertEquals(RSQLOperators.IN, RsqlOperation.IN.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.IN.context)
    }

    @Test
    fun `should have correct operator for NOT_IN`() {
        assertEquals(RSQLOperators.NOT_IN, RsqlOperation.NOT_IN.operator)
        assertEquals(ParserContext.COMMON, RsqlOperation.NOT_IN.context)
    }

    @Test
    fun `should have correct operator for IS_NULL`() {
        assertEquals("=isNull=", RsqlOperation.IS_NULL.operator.symbol)
        assertEquals(ParserContext.COMMON, RsqlOperation.IS_NULL.context)
    }

    @Test
    fun `should have correct operator for EQUAL_CI`() {
        assertEquals("=eqci=", RsqlOperation.EQUAL_CI.operator.symbol)
        assertEquals(ParserContext.COMMON, RsqlOperation.EQUAL_CI.context)
    }

    @Test
    fun `should have correct operator for IS_EMPTY`() {
        assertEquals("=isEmpty=", RsqlOperation.IS_EMPTY.operator.symbol)
        assertEquals(ParserContext.COMMON, RsqlOperation.IS_EMPTY.context)
    }

    @Test
    fun `should have correct operator for JSONB_EQ`() {
        assertEquals("=jsonbeq=", RsqlOperation.JSONB_EQ.operator.symbol)
        assertEquals(ParserContext.POSTGRESQL, RsqlOperation.JSONB_EQ.context)
    }

    @Test
    fun `should have correct operator for JSON_EQ`() {
        assertEquals("=jsoneq=", RsqlOperation.JSON_EQ.operator.symbol)
        assertEquals(ParserContext.POSTGRESQL, RsqlOperation.JSON_EQ.context)
    }

    @Test
    fun `getSimpleOperator should return correct operation for EQUAL operator`() {
        val result = RsqlOperation.getSimpleOperator(RSQLOperators.EQUAL)
        assertEquals(RsqlOperation.EQUAL, result)
    }

    @Test
    fun `getSimpleOperator should return correct operation for NOT_EQUAL operator`() {
        val result = RsqlOperation.getSimpleOperator(RSQLOperators.NOT_EQUAL)
        assertEquals(RsqlOperation.NOT_EQUAL, result)
    }

    @Test
    fun `getSimpleOperator should return correct operation for GREATER_THAN operator`() {
        val result = RsqlOperation.getSimpleOperator(RSQLOperators.GREATER_THAN)
        assertEquals(RsqlOperation.GREATER_THAN, result)
    }

    @Test
    fun `getSimpleOperator should return correct operation for IN operator`() {
        val result = RsqlOperation.getSimpleOperator(RSQLOperators.IN)
        assertEquals(RsqlOperation.IN, result)
    }

    @Test
    fun `getSimpleOperator should return null for unknown operator`() {
        val unknownOperator = ComparisonOperator("=unknown=")
        val result = RsqlOperation.getSimpleOperator(unknownOperator)
        assertNull(result)
    }

    @ParameterizedTest
    @EnumSource(value = RsqlOperation::class, names = ["JSONB_EQ", "JSON_EQ"])
    fun `PostgreSQL operations should have POSTGRESQL context`(operation: RsqlOperation) {
        assertEquals(ParserContext.POSTGRESQL, operation.context)
    }

    @ParameterizedTest
    @EnumSource(value = RsqlOperation::class, names = ["EQUAL", "NOT_EQUAL", "GREATER_THAN", "GREATER_THAN_OR_EQUAL", "LESS_THAN", "LESS_THAN_OR_EQUAL", "IN", "NOT_IN", "IS_NULL", "EQUAL_CI", "IS_EMPTY"])
    fun `Common operations should have COMMON context`(operation: RsqlOperation) {
        assertEquals(ParserContext.COMMON, operation.context)
    }

    @Test
    fun `all operations should be retrievable by their operator`() {
        RsqlOperation.entries.forEach { operation ->
            val retrieved = RsqlOperation.getSimpleOperator(operation.operator)
            assertEquals(operation, retrieved, "Should retrieve ${operation.name} by its operator")
        }
    }
}
