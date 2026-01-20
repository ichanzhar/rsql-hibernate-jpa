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

class ComparisonProcessorsTest {

    private lateinit var mockPath: Path<Any>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<Comparable<Any>>

    @BeforeEach
    fun setUp() {
        mockPath = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)
        mockPredicate = mockk(relaxed = true)
        mockExpression = mockk(relaxed = true)

        every { mockPath.get<Comparable<Any>>(any<String>()) } returns mockExpression
    }

    // GtProcessor Tests
    @Test
    fun `GtProcessor should create greaterThan predicate`() {
        every { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), 18 as Comparable<Any>) }
    }

    @Test
    fun `GtProcessor should handle string comparison`() {
        every { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("B"), "B")
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), "B" as Comparable<Any>) }
    }

    // GteProcessor Tests
    @Test
    fun `GteProcessor should create greaterThanOrEqualTo predicate`() {
        every { mockBuilder.greaterThanOrEqualTo(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(18), 18)
        val processor = GteProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.greaterThanOrEqualTo(any<Expression<Comparable<Any>>>(), 18 as Comparable<Any>) }
    }

    // LtProcessor Tests
    @Test
    fun `LtProcessor should create lessThan predicate`() {
        every { mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = LtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), 65 as Comparable<Any>) }
    }

    // LteProcessor Tests
    @Test
    fun `LteProcessor should create lessThanOrEqualTo predicate`() {
        every { mockBuilder.lessThanOrEqualTo(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "age", "age", listOf(65), 65)
        val processor = LteProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lessThanOrEqualTo(any<Expression<Comparable<Any>>>(), 65 as Comparable<Any>) }
    }

    // Comparison edge cases
    @Test
    fun `GtProcessor should handle double values`() {
        every { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "salary", "salary", listOf(50000.50), 50000.50)
        val processor = GtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.greaterThan(any<Expression<Comparable<Any>>>(), 50000.50 as Comparable<Any>) }
    }

    @Test
    fun `LtProcessor should handle negative values`() {
        every { mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), any<Comparable<Any>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "temperature", "temperature", listOf(-10), -10)
        val processor = LtProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lessThan(any<Expression<Comparable<Any>>>(), -10 as Comparable<Any>) }
    }
}
