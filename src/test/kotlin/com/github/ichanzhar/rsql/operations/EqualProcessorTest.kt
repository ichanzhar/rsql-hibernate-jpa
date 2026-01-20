package com.github.ichanzhar.rsql.operations

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EqualProcessorTest {

    private lateinit var mockPath: Path<Any>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<String>

    @BeforeEach
    fun setUp() {
        mockPath = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)
        mockPredicate = mockk(relaxed = true)
        mockExpression = mockk(relaxed = true)

        every { mockPath.get<String>(any<String>()) } returns mockExpression
    }

    @Test
    fun `should create equal predicate for simple value`() {
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John"), "John")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.equal(any<Expression<*>>(), "John") }
    }

    @Test
    fun `should create like predicate for wildcard prefix pattern`() {
        every { mockBuilder.like(any<Expression<String>>(), any<String>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*John"), "*John")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.like(any<Expression<String>>(), "%John") }
    }

    @Test
    fun `should create like predicate for wildcard suffix pattern`() {
        every { mockBuilder.like(any<Expression<String>>(), any<String>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John*"), "John*")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.like(any<Expression<String>>(), "John%") }
    }

    @Test
    fun `should create like predicate for wildcard prefix and suffix pattern`() {
        every { mockBuilder.like(any<Expression<String>>(), any<String>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*John*"), "*John*")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.like(any<Expression<String>>(), "%John%") }
    }

    @Test
    fun `should create isNull predicate for null argument`() {
        every { mockBuilder.isNull(any<Expression<*>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "email", "email", listOf<Any?>(null), null)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.isNull(any<Expression<*>>()) }
    }

    @Test
    fun `should handle integer value`() {
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(25), 25)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.equal(any<Expression<*>>(), 25) }
    }

    @Test
    fun `should handle boolean value`() {
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "active", "active", listOf(true), true)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.equal(any<Expression<*>>(), true) }
    }

    @Test
    fun `isLikeExpression should return true for prefix wildcard`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*test"), "*test")
        val processor = EqualProcessor(params)
        assertTrue(processor.isLikeExpression())
    }

    @Test
    fun `isLikeExpression should return true for suffix wildcard`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("test*"), "test*")
        val processor = EqualProcessor(params)
        assertTrue(processor.isLikeExpression())
    }

    @Test
    fun `isLikeExpression should return false for no wildcard`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("test"), "test")
        val processor = EqualProcessor(params)
        assertFalse(processor.isLikeExpression())
    }

    @Test
    fun `isLikeExpression should return false for non-string argument`() {
        val params = Params(mockPath, mockBuilder, "age", "age", listOf(25), 25)
        val processor = EqualProcessor(params)
        assertFalse(processor.isLikeExpression())
    }

    @Test
    fun `getFormattedLikePattern should replace asterisks with percent signs`() {
        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*test*"), "*test*")
        val processor = EqualProcessor(params)
        assertEquals("%test%", processor.getFormattedLikePattern())
    }
}
