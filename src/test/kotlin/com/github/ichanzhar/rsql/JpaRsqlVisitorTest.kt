package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.utils.RsqlParserFactory
import cz.jirutka.rsql.parser.ast.AndNode
import cz.jirutka.rsql.parser.ast.ComparisonNode
import cz.jirutka.rsql.parser.ast.OrNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.jpa.domain.Specification

class JpaRsqlVisitorTest {

    @Test
    fun `should create visitor with default distinct false`() {
        val visitor = JpaRsqlVisitor<TestUser>()
        assertNotNull(visitor)
    }

    @Test
    fun `should create visitor with distinct true`() {
        val visitor = JpaRsqlVisitor<TestUser>(distinct = true)
        assertNotNull(visitor)
    }

    @Test
    fun `should visit ComparisonNode and return Specification`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John") as ComparisonNode

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = visitor.visit(node, null)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should visit AndNode and return Specification`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John;age=gt=18") as AndNode

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = visitor.visit(node, null)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should visit OrNode and return Specification`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John,name==Jane") as OrNode

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = visitor.visit(node, null)

        assertNotNull(specification)
        assertTrue(specification is Specification)
    }

    @Test
    fun `should handle complex nested query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("(name==John,name==Jane);age=gt=18")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle EQUAL operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle NOT_EQUAL operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name!=John")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle GREATER_THAN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=gt=18")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle GREATER_THAN_OR_EQUAL operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=ge=18")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle LESS_THAN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=lt=65")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle LESS_THAN_OR_EQUAL operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=le=65")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=in=(ACTIVE,PENDING)")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle NOT_IN operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=out=(INACTIVE)")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IS_NULL operator with true`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("email=isNull=true")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IS_NULL operator with false`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("email=isNull=false")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle EQUAL_CI operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name=eqci=john")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle IS_EMPTY operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("tags=isEmpty=true")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle wildcard prefix pattern`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==*John")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle wildcard suffix pattern`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John*")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle wildcard prefix and suffix pattern`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==*John*")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle nested property`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("department.name==Engineering")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle deeply nested property`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("department.parent.name==Tech")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle PostgreSQL JSON_EQ operator after init`() {
        val parser = RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        val node = parser.parse("metadata=jsoneq=value")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }

    @Test
    fun `should handle PostgreSQL JSONB_EQ operator after init`() {
        val parser = RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        val node = parser.parse("data=jsonbeq=value")

        val visitor = JpaRsqlVisitor<TestUser>()
        val specification = node.accept(visitor)

        assertNotNull(specification)
    }
}
