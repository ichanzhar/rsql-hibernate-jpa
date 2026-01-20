package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class CollectionProcessorsTest {

    private lateinit var mockPath: Path<*>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<Any>
    private lateinit var mockInPredicate: CriteriaBuilder.In<Any>

    @BeforeEach
    fun setUp() {
        mockPath = mock(Path::class.java) as Path<*>
        mockBuilder = mock(CriteriaBuilder::class.java)
        mockPredicate = mock(Predicate::class.java)
        mockExpression = mock(Expression::class.java) as Expression<Any>
        mockInPredicate = mock(CriteriaBuilder.In::class.java) as CriteriaBuilder.In<Any>

        whenever(mockPath.get<Any>(any<String>())).thenReturn(mockExpression)
        whenever(mockExpression.`in`(any<Collection<*>>())).thenReturn(mockInPredicate)
    }

    // InProcessor Tests
    @Test
    fun `InProcessor should create in predicate for single value`() {
        val args = listOf("ACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "ACTIVE")
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockExpression).`in`(args)
    }

    @Test
    fun `InProcessor should create in predicate for multiple values`() {
        val args = listOf("ACTIVE", "PENDING", "INACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "ACTIVE")
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockExpression).`in`(args)
    }

    @Test
    fun `InProcessor should handle integer values`() {
        val args = listOf(1, 2, 3, 4, 5)
        val params = Params(mockPath, mockBuilder, "id", "id", args, 1)
        val processor = InProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockExpression).`in`(args)
    }

    // NotInProcessor Tests
    @Test
    fun `NotInProcessor should create not in predicate`() {
        whenever(mockInPredicate.not()).thenReturn(mockPredicate)

        val args = listOf("INACTIVE")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "INACTIVE")
        val processor = NotInProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockExpression).`in`(args)
        verify(mockInPredicate).not()
    }

    @Test
    fun `NotInProcessor should handle multiple excluded values`() {
        whenever(mockInPredicate.not()).thenReturn(mockPredicate)

        val args = listOf("INACTIVE", "DELETED", "BANNED")
        val params = Params(mockPath, mockBuilder, "status", "status", args, "INACTIVE")
        val processor = NotInProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockExpression).`in`(args)
        verify(mockInPredicate).not()
    }
}
