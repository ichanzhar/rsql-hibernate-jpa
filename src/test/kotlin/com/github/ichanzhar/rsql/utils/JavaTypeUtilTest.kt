package com.github.ichanzhar.rsql.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JavaTypeUtilTest {

    @Test
    fun `should return Boolean class for java_lang_Boolean`() {
        val result = JavaTypeUtil.getPropertyJavaType(java.lang.Boolean::class.java)
        assertEquals(Boolean::class.java, result)
    }

    @Test
    fun `should return Boolean class for primitive boolean`() {
        val result = JavaTypeUtil.getPropertyJavaType(Boolean::class.javaPrimitiveType)
        assertEquals(Boolean::class.java, result)
    }

    @Test
    fun `should return Byte class for primitive byte`() {
        val result = JavaTypeUtil.getPropertyJavaType(Byte::class.javaPrimitiveType)
        assertEquals(Byte::class.java, result)
    }

    @Test
    fun `should return Character class for primitive char`() {
        val result = JavaTypeUtil.getPropertyJavaType(Char::class.javaPrimitiveType)
        assertEquals(Character::class.java, result)
    }

    @Test
    fun `should return Double class for primitive double`() {
        val result = JavaTypeUtil.getPropertyJavaType(Double::class.javaPrimitiveType)
        assertEquals(Double::class.java, result)
    }

    @Test
    fun `should return Float class for primitive float`() {
        val result = JavaTypeUtil.getPropertyJavaType(Float::class.javaPrimitiveType)
        assertEquals(Float::class.java, result)
    }

    @Test
    fun `should return Integer class for primitive int`() {
        val result = JavaTypeUtil.getPropertyJavaType(Int::class.javaPrimitiveType)
        assertEquals(Integer::class.java, result)
    }

    @Test
    fun `should return Long class for primitive long`() {
        val result = JavaTypeUtil.getPropertyJavaType(Long::class.javaPrimitiveType)
        assertEquals(Long::class.java, result)
    }

    @Test
    fun `should return Short class for primitive short`() {
        val result = JavaTypeUtil.getPropertyJavaType(Short::class.javaPrimitiveType)
        assertEquals(Short::class.java, result)
    }

    @Test
    fun `should return same class for non-primitive types`() {
        val result = JavaTypeUtil.getPropertyJavaType(String::class.java)
        assertEquals(String::class.java, result)
    }

    @Test
    fun `should return null for null input`() {
        val result = JavaTypeUtil.getPropertyJavaType(null)
        assertNull(result)
    }

    @Test
    fun `should return same class for custom classes`() {
        class CustomClass
        val result = JavaTypeUtil.getPropertyJavaType(CustomClass::class.java)
        assertEquals(CustomClass::class.java, result)
    }

    @Test
    fun `should return same class for List type`() {
        val result = JavaTypeUtil.getPropertyJavaType(List::class.java)
        assertEquals(List::class.java, result)
    }

    @Test
    fun `should return same class for Map type`() {
        val result = JavaTypeUtil.getPropertyJavaType(Map::class.java)
        assertEquals(Map::class.java, result)
    }
}
