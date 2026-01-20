package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.utils.RsqlParserFactory
import cz.jirutka.rsql.parser.ast.ComparisonNode
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import cz.jirutka.rsql.parser.ast.LogicalNode
import cz.jirutka.rsql.parser.ast.LogicalOperator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.jpa.domain.Specification

class GenericRsqlSpecBuilderTest {

    @Test
    fun `should create builder with distinct false`() {
        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        assertFalse(builder.distinct)
    }

    @Test
    fun `should create builder with distinct true`() {
        val builder = GenericRsqlSpecBuilder<TestUser>(true)
        assertTrue(builder.distinct)
    }

    @Test
    fun `should create specification from ComparisonNode`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should create specification from AND LogicalNode`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John;age=gt=18")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should create specification from OR LogicalNode`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John,name==Jane")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should create specification from complex query with AND and OR`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("(name==John,name==Jane);age=gt=18")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should create specification from nested property query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("department.name==Engineering")

        val builder = GenericRsqlSpecBuilder<TestDepartment>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=in=(ACTIVE,PENDING)")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle NOT_IN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=out=(INACTIVE)")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IS_NULL operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("email=isNull=true")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle wildcard pattern`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==*John*")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle multiple AND conditions`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John;age=gt=18;active==true")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle multiple OR conditions`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John,name==Jane,name==Bob")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle comparison operators`() {
        val parser = RsqlParserFactory.instance()

        val queries = listOf(
            "age=gt=18",
            "age=ge=18",
            "age=lt=65",
            "age=le=65",
            "name!=John"
        )

        val builder = GenericRsqlSpecBuilder<TestUser>(false)

        queries.forEach { query ->
            val node = parser.parse(query)
            val specification = builder.createSpecification(node)
            assertNotNull(specification, "Should create specification for query: $query")
        }
    }

    @Test
    fun `should handle case-insensitive equality`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name=eqci=john")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IS_EMPTY operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("tags=isEmpty=true")

        val builder = GenericRsqlSpecBuilder<TestUser>(false)
        val specification = builder.createSpecification(node)

        assertNotNull(specification)
    }
}
