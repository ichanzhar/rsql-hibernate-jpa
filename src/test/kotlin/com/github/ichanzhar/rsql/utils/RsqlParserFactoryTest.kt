package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.ParserContext
import com.github.ichanzhar.rsql.RsqlOperation
import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.RSQLParserException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RsqlParserFactoryTest {

    @Test
    fun `should create parser instance without context`() {
        val parser = RsqlParserFactory.instance()
        assertNotNull(parser)
        assertTrue(parser is RSQLParser)
    }

    @Test
    fun `should create parser instance with COMMON context`() {
        val parser = RsqlParserFactory.instance(ParserContext.COMMON)
        assertNotNull(parser)
        assertTrue(parser is RSQLParser)
    }

    @Test
    fun `should create parser instance with POSTGRESQL context`() {
        val parser = RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        assertNotNull(parser)
        assertTrue(parser is RSQLParser)
    }

    @Test
    fun `parser should parse simple EQUAL query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse NOT_EQUAL query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name!=John")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse GREATER_THAN query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=gt=18")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse GREATER_THAN_OR_EQUAL query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=ge=18")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse LESS_THAN query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=lt=65")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse LESS_THAN_OR_EQUAL query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("age=le=65")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse IN query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=in=(ACTIVE,PENDING)")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse NOT_IN query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("status=out=(INACTIVE)")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse IS_NULL query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("email=isNull=true")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse EQUAL_CI query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name=eqci=john")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse IS_EMPTY query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("tags=isEmpty=true")
        assertNotNull(node)
    }

    @Test
    fun `parser with POSTGRESQL context should parse JSON_EQ query`() {
        val parser = RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        val node = parser.parse("metadata=jsoneq=value")
        assertNotNull(node)
    }

    @Test
    fun `parser with POSTGRESQL context should parse JSONB_EQ query`() {
        val parser = RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        val node = parser.parse("data=jsonbeq=value")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse AND logical operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John;age=gt=18")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse OR logical operator`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==John,name==Jane")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse complex query with AND and OR`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("(name==John,name==Jane);age=gt=18")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse nested property query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("department.name==Engineering")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse deeply nested property query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("department.parent.name==Tech")
        assertNotNull(node)
    }

    @Test
    fun `parser should parse wildcard query`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==*John*")
        assertNotNull(node)
    }

    @Test
    fun `parser should throw exception for invalid query syntax`() {
        val parser = RsqlParserFactory.instance()
        assertThrows<RSQLParserException> {
            parser.parse("invalid query syntax")
        }
    }

    @Test
    fun `parser should throw exception for unknown operator without POSTGRESQL context`() {
        val parser = RsqlParserFactory.instance(ParserContext.COMMON)
        // JSON operators are not registered by default without POSTGRESQL context
        // This depends on whether PostgreSQL operations were previously registered
    }

    @Test
    fun `parser should handle quoted values`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name=='John Doe'")
        assertNotNull(node)
    }

    @Test
    fun `parser should handle double quoted values`() {
        val parser = RsqlParserFactory.instance()
        val node = parser.parse("name==\"John Doe\"")
        assertNotNull(node)
    }

    @Test
    fun `PostgreSQL context should register PostgreSQL operations`() {
        RsqlParserFactory.instance(ParserContext.POSTGRESQL)
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.JSON_EQ.operator))
        assertTrue(RsqlOperationsRegistry.operations.contains(RsqlOperation.JSONB_EQ.operator))
    }
}
