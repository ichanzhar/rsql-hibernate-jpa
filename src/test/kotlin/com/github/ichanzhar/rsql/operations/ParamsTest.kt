package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class ParamsTest {

    private val mockPath: Path<*> = mock(Path::class.java) as Path<*>
    private val mockBuilder: CriteriaBuilder = mock(CriteriaBuilder::class.java)

    @Test
    fun `should create Params with all properties`() {
        val args = listOf("value1", "value2")
        val params = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "user.name",
            args = args,
            argument = "value1"
        )

        assertEquals(mockPath, params.root)
        assertEquals(mockBuilder, params.builder)
        assertEquals("name", params.property)
        assertEquals("user.name", params.globalProperty)
        assertEquals(args, params.args)
        assertEquals("value1", params.argument)
    }

    @Test
    fun `should allow null argument`() {
        val params = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "email",
            globalProperty = "email",
            args = emptyList(),
            argument = null
        )

        assertNull(params.argument)
    }

    @Test
    fun `should support data class copy`() {
        val original = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("original"),
            argument = "original"
        )

        val copied = original.copy(property = "email", argument = "new@email.com")

        assertEquals("name", original.property)
        assertEquals("email", copied.property)
        assertEquals("new@email.com", copied.argument)
    }

    @Test
    fun `should support data class equals`() {
        val params1 = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("John"),
            argument = "John"
        )

        val params2 = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("John"),
            argument = "John"
        )

        assertEquals(params1, params2)
    }

    @Test
    fun `should support data class hashCode`() {
        val params1 = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("John"),
            argument = "John"
        )

        val params2 = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("John"),
            argument = "John"
        )

        assertEquals(params1.hashCode(), params2.hashCode())
    }

    @Test
    fun `should support mutable properties`() {
        val params = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = listOf("original"),
            argument = "original"
        )

        params.property = "updatedProperty"
        params.argument = "updatedArgument"

        assertEquals("updatedProperty", params.property)
        assertEquals("updatedArgument", params.argument)
    }

    @Test
    fun `should handle empty args list`() {
        val params = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "name",
            globalProperty = "name",
            args = emptyList(),
            argument = null
        )

        assertTrue(params.args.isEmpty())
    }

    @Test
    fun `should handle args with different types`() {
        val mixedArgs = listOf<Any>("string", 123, true, 3.14)
        val params = Params(
            root = mockPath,
            builder = mockBuilder,
            property = "field",
            globalProperty = "field",
            args = mixedArgs,
            argument = "string"
        )

        assertEquals(4, params.args.size)
        assertEquals("string", params.args[0])
        assertEquals(123, params.args[1])
        assertEquals(true, params.args[2])
        assertEquals(3.14, params.args[3])
    }
}
