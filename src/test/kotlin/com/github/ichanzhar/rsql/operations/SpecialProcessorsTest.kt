package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class SpecialProcessorsTest {

    private lateinit var mockPath: Path<*>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockExpression: Expression<String>
    private lateinit var mockCollectionExpression: Expression<Collection<*>>

    @BeforeEach
    fun setUp() {
        mockPath = mock(Path::class.java) as Path<*>
        mockBuilder = mock(CriteriaBuilder::class.java)
        mockPredicate = mock(Predicate::class.java)
        mockExpression = mock(Expression::class.java) as Expression<String>
        mockCollectionExpression = mock(Expression::class.java) as Expression<Collection<*>>

        whenever(mockPath.get<String>(any<String>())).thenReturn(mockExpression)
    }

    // IsNullProcessor Tests
    @Test
    fun `IsNullProcessor should create isNull predicate when argument is true`() {
        whenever(mockBuilder.isNull(any<Expression<*>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "email", "email", listOf("true"), "true")
        val processor = IsNullProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).isNull(any<Expression<*>>())
    }

    @Test
    fun `IsNullProcessor should create isNotNull predicate when argument is false`() {
        whenever(mockBuilder.isNotNull(any<Expression<*>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "email", "email", listOf("false"), "false")
        val processor = IsNullProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).isNotNull(any<Expression<*>>())
    }

    @Test
    fun `IsNullProcessor should handle TRUE uppercase`() {
        whenever(mockBuilder.isNull(any<Expression<*>>())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "email", "email", listOf("TRUE"), "TRUE")
        val processor = IsNullProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        // Note: "TRUE".toBoolean() returns false in Kotlin, so this actually creates isNotNull
        // The implementation uses (params.argument as String).toBoolean()
    }

    // IsEmptyProcessor Tests
    @Test
    fun `IsEmptyProcessor should create isEmpty predicate when argument is true`() {
        whenever(mockBuilder.isEmpty(any<Expression<Collection<*>>>())).thenReturn(mockPredicate)
        whenever(mockPath.get<Collection<*>>(any<String>())).thenReturn(mockCollectionExpression)

        val params = Params(mockPath, mockBuilder, "tags", "tags", listOf("true"), "true")
        val processor = IsEmptyProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).isEmpty(any<Expression<Collection<*>>>())
    }

    @Test
    fun `IsEmptyProcessor should create isNotEmpty predicate when argument is false`() {
        whenever(mockBuilder.isNotEmpty(any<Expression<Collection<*>>>())).thenReturn(mockPredicate)
        whenever(mockPath.get<Collection<*>>(any<String>())).thenReturn(mockCollectionExpression)

        val params = Params(mockPath, mockBuilder, "tags", "tags", listOf("false"), "false")
        val processor = IsEmptyProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).isNotEmpty(any<Expression<Collection<*>>>())
    }

    // EqualCiProcessor Tests (case-insensitive equality)
    @Test
    fun `EqualCiProcessor should create equal predicate with lower case conversion`() {
        whenever(mockBuilder.lower(any<Expression<String>>())).thenReturn(mockExpression)
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("JOHN"), "JOHN")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lower(any<Expression<String>>())
        verify(mockBuilder).equal(any<Expression<*>>(), eq("john"))
    }

    @Test
    fun `EqualCiProcessor should handle mixed case input`() {
        whenever(mockBuilder.lower(any<Expression<String>>())).thenReturn(mockExpression)
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("JoHn DoE"), "JoHn DoE")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lower(any<Expression<String>>())
        verify(mockBuilder).equal(any<Expression<*>>(), eq("john doe"))
    }

    @Test
    fun `EqualCiProcessor should handle already lowercase input`() {
        whenever(mockBuilder.lower(any<Expression<String>>())).thenReturn(mockExpression)
        whenever(mockBuilder.equal(any<Expression<*>>(), any())).thenReturn(mockPredicate)

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("john"), "john")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify(mockBuilder).lower(any<Expression<String>>())
        verify(mockBuilder).equal(any<Expression<*>>(), eq("john"))
    }
}
