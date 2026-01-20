package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class EqualProcessorTest {

    private lateinit var mockPath: Path<*>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<String>

    @BeforeEach
    fun setUp() {
        mockPath = mock(Path::class.java) as Path<*>
        mockBuilder = mock(CriteriaBuilder::class.java)
        mockPredicate = mock(Predicate::class.java)
        mockExpression = mock(Expression::class.java) as Expression<String>

        whenever(mockPath.get<String>(any<String>())).thenReturn(mockExpression)
    }

    @Test
    fun `should create equal predicate for simple value`() {
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John"), "John")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).equal(any<Expression<*>>(), eq("John"))
    }

    @Test
    fun `should create like predicate for wildcard prefix pattern`() {
        whenever(mockBuilder.like(any<Expression<String>>(), any<String>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*John"), "*John")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).like(any<Expression<String>>(), eq("%John"))
    }

    @Test
    fun `should create like predicate for wildcard suffix pattern`() {
        whenever(mockBuilder.like(any<Expression<String>>(), any<String>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("John*"), "John*")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).like(any<Expression<String>>(), eq("John%"))
    }

    @Test
    fun `should create like predicate for wildcard prefix and suffix pattern`() {
        whenever(mockBuilder.like(any<Expression<String>>(), any<String>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("*John*"), "*John*")
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).like(any<Expression<String>>(), eq("%John%"))
    }

    @Test
    fun `should create isNull predicate for null argument`() {
        whenever(mockBuilder.isNull(any<Expression<*>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "email", "email", listOf<Any?>(null), null)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).isNull(any<Expression<*>>())
    }

    @Test
    fun `should handle integer value`() {
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(25), 25)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).equal(any<Expression<*>>(), eq(25))
    }

    @Test
    fun `should handle boolean value`() {
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "active", "active", listOf(true), true)
        val processor = EqualProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).equal(any<Expression<*>>(), eq(true))
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
