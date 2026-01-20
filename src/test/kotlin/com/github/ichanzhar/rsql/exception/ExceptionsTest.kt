package com.github.ichanzhar.rsql.exception

import com.github.ichanzhar.rsql.Status
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ExceptionsTest {

    @Test
    fun `InvalidDateFormatException should contain argument and property in message`() {
        val exception = InvalidDateFormatException("invalid-date", "createdAt")

        assertTrue(exception.message!!.contains("invalid-date"))
        assertTrue(exception.message!!.contains("createdAt"))
        assertTrue(exception.message!!.contains("invalid date format"))
    }

    @Test
    fun `InvalidDateFormatException should handle null argument`() {
        val exception = InvalidDateFormatException(null, "createdAt")

        assertTrue(exception.message!!.contains("null"))
        assertTrue(exception.message!!.contains("createdAt"))
    }

    @Test
    fun `InvalidDateFormatException should handle null property`() {
        val exception = InvalidDateFormatException("invalid-date", null)

        assertTrue(exception.message!!.contains("invalid-date"))
        assertTrue(exception.message!!.contains("null"))
    }

    @Test
    fun `InvalidDateFormatException should be a RuntimeException`() {
        val exception = InvalidDateFormatException("invalid", "field")
        assertTrue(exception is RuntimeException)
    }

    @Test
    fun `InvalidEnumValueException should contain value and enum name in message`() {
        val exception = InvalidEnumValueException(Status::class.java, "INVALID_STATUS")

        assertTrue(exception.message!!.contains("INVALID_STATUS"))
        assertTrue(exception.message!!.contains("Status"))
    }

    @Test
    fun `InvalidEnumValueException should handle null java type`() {
        val exception = InvalidEnumValueException(null, "INVALID")

        assertTrue(exception.message!!.contains("INVALID"))
        assertTrue(exception.message!!.contains("null"))
    }

    @Test
    fun `InvalidEnumValueException should be a RuntimeException`() {
        val exception = InvalidEnumValueException(Status::class.java, "INVALID")
        assertTrue(exception is RuntimeException)
    }

    @Test
    fun `InvalidEnumValueException should show simple name of enum class`() {
        val exception = InvalidEnumValueException(Status::class.java, "UNKNOWN")

        // Should contain "Status" not the full class name
        assertTrue(exception.message!!.contains("Status"))
        assertFalse(exception.message!!.contains("com.github.ichanzhar"))
    }
}
