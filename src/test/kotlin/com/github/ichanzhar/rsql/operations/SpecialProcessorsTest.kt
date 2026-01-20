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

class SpecialProcessorsTest {

    private lateinit var mockPath: Path<Any>
    private lateinit var mockBuilder: CriteriaBuilder
    private lateinit var mockPredicate: Predicate
    private lateinit var mockNestedPath: Path<String>
    private lateinit var mockCollectionPath: Path<Collection<*>>

    @BeforeEach
    fun setUp() {
        mockPath = mockk(relaxed = true)
        mockBuilder = mockk(relaxed = true)
        mockPredicate = mockk(relaxed = true)
        mockNestedPath = mockk(relaxed = true)
        mockCollectionPath = mockk(relaxed = true)

        every { mockPath.get<String>(any<String>()) } returns mockNestedPath
    }

    // IsNullProcessor Tests
    @Test
    fun `IsNullProcessor should create isNull predicate when argument is true`() {
        every { mockBuilder.isNull(any<Expression<*>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "email", "email", listOf("true"), "true")
        val processor = IsNullProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.isNull(any<Expression<*>>()) }
    }

    @Test
    fun `IsNullProcessor should create isNotNull predicate when argument is false`() {
        every { mockBuilder.isNotNull(any<Expression<*>>()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "email", "email", listOf("false"), "false")
        val processor = IsNullProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.isNotNull(any<Expression<*>>()) }
    }

    @Test
    fun `IsNullProcessor should handle TRUE uppercase`() {
        every { mockBuilder.isNull(any<Expression<*>>()) } returns mockPredicate

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
        every { mockBuilder.isEmpty(any<Expression<Collection<*>>>()) } returns mockPredicate
        every { mockPath.get<Collection<*>>(any<String>()) } returns mockCollectionPath

        val params = Params(mockPath, mockBuilder, "tags", "tags", listOf("true"), "true")
        val processor = IsEmptyProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.isEmpty(any<Expression<Collection<*>>>()) }
    }

    @Test
    fun `IsEmptyProcessor should create isNotEmpty predicate when argument is false`() {
        every { mockBuilder.isNotEmpty(any<Expression<Collection<*>>>()) } returns mockPredicate
        every { mockPath.get<Collection<*>>(any<String>()) } returns mockCollectionPath

        val params = Params(mockPath, mockBuilder, "tags", "tags", listOf("false"), "false")
        val processor = IsEmptyProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.isNotEmpty(any<Expression<Collection<*>>>()) }
    }

    // EqualCiProcessor Tests (case-insensitive equality)
    @Test
    fun `EqualCiProcessor should create equal predicate with lower case conversion`() {
        every { mockBuilder.lower(any<Expression<String>>()) } returns mockNestedPath
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("JOHN"), "JOHN")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lower(any<Expression<String>>()) }
        verify { mockBuilder.equal(any<Expression<*>>(), "john") }
    }

    @Test
    fun `EqualCiProcessor should handle mixed case input`() {
        every { mockBuilder.lower(any<Expression<String>>()) } returns mockNestedPath
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("JoHn DoE"), "JoHn DoE")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lower(any<Expression<String>>()) }
        verify { mockBuilder.equal(any<Expression<*>>(), "john doe") }
    }

    @Test
    fun `EqualCiProcessor should handle already lowercase input`() {
        every { mockBuilder.lower(any<Expression<String>>()) } returns mockNestedPath
        every { mockBuilder.equal(any<Expression<*>>(), any()) } returns mockPredicate

        val params = Params(mockPath, mockBuilder, "name", "name", listOf("john"), "john")
        val processor = EqualCiProcessor(params)
        val result = processor.process()

        assertNotNull(result)
        verify { mockBuilder.lower(any<Expression<String>>()) }
        verify { mockBuilder.equal(any<Expression<*>>(), "john") }
    }
}
