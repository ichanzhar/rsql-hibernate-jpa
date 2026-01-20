package com.github.ichanzhar.rsql.operations

import com.github.ichanzhar.rsql.RsqlOperation
import com.github.ichanzhar.rsql.utils.RsqlOperationsRegistry
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProcessorsFactoryTest {

    private lateinit var mockPath: Path<Any>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockNestedPath: Path<Any>

    @BeforeEach
    fun setUp() {
        mockPath = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)
        mockNestedPath = mockk(relaxed = true)

        every { mockPath.get<Any>(any<String>()) } returns mockNestedPath
    }

    @Test
    fun `should return EqualProcessor for EQUAL operator`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John"), "John")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.EQUAL.operator, params)
        assertTrue(processor is EqualProcessor)
    }

    @Test
    fun `should return NotEqualProcessor for NOT_EQUAL operator`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John"), "John")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.NOT_EQUAL.operator, params)
        assertTrue(processor is NotEqualProcessor)
    }

    @Test
    fun `should return GtProcessor for GREATER_THAN operator`() {
        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.GREATER_THAN.operator, params)
        assertTrue(processor is GtProcessor)
    }

    @Test
    fun `should return GteProcessor for GREATER_THAN_OR_EQUAL operator`() {
        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.GREATER_THAN_OR_EQUAL.operator, params)
        assertTrue(processor is GteProcessor)
    }

    @Test
    fun `should return LtProcessor for LESS_THAN operator`() {
        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.LESS_THAN.operator, params)
        assertTrue(processor is LtProcessor)
    }

    @Test
    fun `should return LteProcessor for LESS_THAN_OR_EQUAL operator`() {
        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.LESS_THAN_OR_EQUAL.operator, params)
        assertTrue(processor is LteProcessor)
    }

    @Test
    fun `should return InProcessor for IN operator`() {
        val params = Params(mockPath, mockBuilder, "status", "status", listOf("ACTIVE", "PENDING"), "ACTIVE")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.IN.operator, params)
        assertTrue(processor is InProcessor)
    }

    @Test
    fun `should return NotInProcessor for NOT_IN operator`() {
        val params = Params(mockPath, mockBuilder, "status", "status", listOf("INACTIVE"), "INACTIVE")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.NOT_IN.operator, params)
        assertTrue(processor is NotInProcessor)
    }

    @Test
    fun `should return IsNullProcessor for IS_NULL operator`() {
        val params = Params(mockPath, mockBuilder, "email", "email", listOf("true"), "true")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.IS_NULL.operator, params)
        assertTrue(processor is IsNullProcessor)
    }

    @Test
    fun `should return EqualCiProcessor for EQUAL_CI operator`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("john"), "john")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.EQUAL_CI.operator, params)
        assertTrue(processor is EqualCiProcessor)
    }

    @Test
    fun `should return IsEmptyProcessor for IS_EMPTY operator`() {
        val params = Params(mockPath, mockBuilder, "tags", "tags", listOf("true"), "true")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.IS_EMPTY.operator, params)
        assertTrue(processor is IsEmptyProcessor)
    }

    @Test
    fun `should return JsonEqualProcessor for JSON_EQ operator after PostgreSQL init`() {
        RsqlOperationsRegistry.initDefaultPostgresOperation()
        val params = Params(mockPath, mockBuilder, "metadata", "metadata", listOf("value"), "value")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.JSON_EQ.operator, params)
        assertTrue(processor is JsonEqualProcessor)
    }

    @Test
    fun `should return JsonbEqualProcessor for JSONB_EQ operator after PostgreSQL init`() {
        RsqlOperationsRegistry.initDefaultPostgresOperation()
        val params = Params(mockPath, mockBuilder, "data", "data", listOf("value"), "value")
        val processor = ProcessorsFactory.getProcessor(RsqlOperation.JSONB_EQ.operator, params)
        assertTrue(processor is JsonbEqualProcessor)
    }

    @Test
    fun `all registered operators should have corresponding processors`() {
        RsqlOperationsRegistry.operations.forEach { operator ->
            val params = Params(mockPath, mockBuilder, "property", "property", listOf("value"), "value")
            val processor = ProcessorsFactory.getProcessor(operator, params)
            assertNotNull(processor, "Processor should exist for operator: ${operator.symbol}")
            assertTrue(processor is Processor)
        }
    }
}
