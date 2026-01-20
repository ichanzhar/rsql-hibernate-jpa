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

class ComparisonProcessorsTest {

    private lateinit var mockPath: Path<*>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<Comparable<Any>>

    @BeforeEach
    fun setUp() {
        mockPath = mock(Path::class.java) as Path<*>
        mockBuilder = mock(CriteriaBuilder::class.java)
        mockPredicate = mock(Predicate::class.java)
        mockExpression = mock(Expression::class.java) as Expression<Comparable<Any>>

        whenever(mockPath.get<Comparable<Any>>(any<String>())).thenReturn(mockExpression)
    }

    // GtProcessor Tests
    @Test
    fun `GtProcessor should create greaterThan predicate`() {
        whenever(mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).greaterThan(any<Expression<Comparable<Any>>>(), eq(18 as Comparable<Any>))
    }

    @Test
    fun `GtProcessor should handle string comparison`() {
        whenever(mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("B"), "B")
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).greaterThan(any<Expression<Comparable<Any>>>(), eq("B" as Comparable<Any>))
    }

    // GteProcessor Tests
    @Test
    fun `GteProcessor should create greaterThanOrEqualTo predicate`() {
        whenever(mockBuilder.greaterThanOrEqualTo(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = GteProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).greaterThanOrEqualTo(any<Expression<Comparable<Any>>>(), eq(18 as Comparable<Any>))
    }

    // LtProcessor Tests
    @Test
    fun `LtProcessor should create lessThan predicate`() {
        whenever(mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = LtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lessThan(any<Expression<Comparable<Any>>>(), eq(65 as Comparable<Any>))
    }

    // LteProcessor Tests
    @Test
    fun `LteProcessor should create lessThanOrEqualTo predicate`() {
        whenever(mockBuilder.lessThanOrEqualTo(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = LteProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lessThanOrEqualTo(any<Expression<Comparable<Any>>>(), eq(65 as Comparable<Any>))
    }

    // Comparison edge cases
    @Test
    fun `GtProcessor should handle double values`() {
        whenever(mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "salary", "salary", listOf(50000.50), 50000.50)
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).greaterThan(any<Expression<Comparable<Any>>>(), eq(50000.50 as Comparable<Any>))
    }

    @Test
    fun `LtProcessor should handle negative values`() {
        whenever(mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "temperature", "temperature", listOf(-10), -10)
        val processor = LtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lessThan(any<Expression<Comparable<Any>>>(), eq(-10 as Comparable<Any>))
    }
}
