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

class CollectionProcessorsTest {

    private lateinit var mockPath: Path<Any>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<Any>
    private lateinit var mockInPredicate: CriteriaBuilder.In<Any>

    @BeforeEach
    fun setUp() {
        mockPath = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)
        mockPredicate = mockk(relaxed = true)
        mockExpression = mockk(relaxed = true)
        mockInPredicate = mockk(relaxed = true)

        every { mockPath.get<Any>(any<String>()) } returns mockExpression
        every { mockExpression.`in`(any<Collection<*>>()) } returns mockInPredicate
    }

    // InProcessor Tests
    @Test
    fun `InProcessor should create in predicate for single value`() {
        val args = listOf("ACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "ACTIVE")
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockExpression.`in`(args) }
    }

    @Test
    fun `InProcessor should create in predicate for multiple values`() {
        val args = listOf("ACTIVE", "PENDING", "INACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "ACTIVE")
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockExpression.`in`(args) }
    }

    @Test
    fun `InProcessor should handle integer values`() {
        val args = listOf(1, 2, 3, 4, 5)
        val params = Params(mockPath, mockBuilder, "id", "id", args, 1)
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockExpression.`in`(args) }
    }

    // NotInProcessor Tests
    @Test
    fun `NotInProcessor should create not in predicate`() {
        every { mockInPredicate.not() } returns mockPredicate

        val args = listOf("INACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "INACTIVE")
        val processor = NotInProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockExpression.`in`(args) }
        verify { mockInPredicate.not() }
    }

    @Test
    fun `NotInProcessor should handle multiple excluded values`() {
        every { mockInPredicate.not() } returns mockPredicate

        val args = listOf("INACTIVE", "DELETED", "BANNED")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "INACTIVE")
        val processor = NotInProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockExpression.`in`(args) }
        verify { mockInPredicate.not() }
    }
}
