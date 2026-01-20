package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.Status
import com.github.ichanzhar.rsql.exception.InvalidDateFormatException
import com.github.ichanzhar.rsql.exception.InvalidEnumValueException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*

class ArgumentConvertorTest {

    @Test
    fun `should convert string to Int`() {
        val result = ArgumentConvertor.castArgument("42", "property", Int::class.java)
        assertEquals(42, result)
        assertTrue(result is Int)
    }

    @Test
    fun `should convert string to Long`() {
        val result = ArgumentConvertor.castArgument("9223372036854775807", "property", Long::class.java)
        assertEquals(9223372036854775807L, result)
        assertTrue(result is Long)
    }

    @Test
    fun `should convert string to BigInteger`() {
        val result = ArgumentConvertor.castArgument("123456789012345678901234567890", "property", BigInteger::class.java)
        assertEquals(BigInteger("123456789012345678901234567890"), result)
        assertTrue(result is BigInteger)
    }

    @Test
    fun `should convert string to Double`() {
        val result = ArgumentConvertor.castArgument("3.14159", "property", Double::class.java)
        assertEquals(3.14159, result)
        assertTrue(result is Double)
    }

    @Test
    fun `should convert string to Float`() {
        val result = ArgumentConvertor.castArgument("3.14", "property", Float::class.java)
        assertEquals(3.14f, result)
        assertTrue(result is Float)
    }

    @Test
    fun `should convert string to BigDecimal`() {
        val result = ArgumentConvertor.castArgument("123.456789", "property", BigDecimal::class.java)
        assertEquals(BigDecimal("123.456789"), result)
        assertTrue(result is BigDecimal)
    }

    @Test
    fun `should convert string to Char`() {
        val result = ArgumentConvertor.castArgument("A", "property", Char::class.java)
        assertEquals('A', result)
        assertTrue(result is Char)
    }

    @Test
    fun `should convert string to Short`() {
        val result = ArgumentConvertor.castArgument("32767", "property", Short::class.java)
        assertEquals(32767.toShort(), result)
        assertTrue(result is Short)
    }

    @ParameterizedTest
    @ValueSource(strings = ["true", "false", "TRUE", "FALSE", "True", "False"])
    fun `should convert string to Boolean`(value: String) {
        val result = ArgumentConvertor.castArgument(value, "property", Boolean::class.java)
        assertEquals(value.toBoolean(), result)
        assertTrue(result is Boolean)
    }

    @Test
    fun `should convert string to UUID`() {
        val uuidString = "550e8400-e29b-41d4-a716-446655440000"
        val result = ArgumentConvertor.castArgument(uuidString, "property", UUID::class.java)
        assertEquals(UUID.fromString(uuidString), result)
        assertTrue(result is UUID)
    }

    @Test
    fun `should convert ISO datetime string to Date`() {
        val result = ArgumentConvertor.castArgument("2023-12-25T10:30:00", "property", Date::class.java)
        assertTrue(result is Date)
    }

    @Test
    fun `should convert custom datetime format to Date`() {
        val result = ArgumentConvertor.castArgument("2023-12-25 10:30:00.000", "property", Date::class.java)
        assertTrue(result is Date)
    }

    @Test
    fun `should throw InvalidDateFormatException for invalid date format`() {
        assertThrows<InvalidDateFormatException> {
            ArgumentConvertor.castArgument("invalid-date", "dateField", java.sql.Timestamp::class.java)
        }
    }

    @Test
    fun `should convert string to LocalDate`() {
        val result = ArgumentConvertor.castArgument("2023-12-25", "property", LocalDate::class.java)
        assertEquals(LocalDate.of(2023, 12, 25), result)
        assertTrue(result is LocalDate)
    }

    @Test
    fun `should convert string to LocalDateTime`() {
        val result = ArgumentConvertor.castArgument("2023-12-25T10:30:00", "property", LocalDateTime::class.java)
        assertEquals(LocalDateTime.of(2023, 12, 25, 10, 30, 0), result)
        assertTrue(result is LocalDateTime)
    }

    @Test
    fun `should convert string to LocalTime`() {
        val result = ArgumentConvertor.castArgument("10:30:00", "property", LocalTime::class.java)
        assertEquals(LocalTime.of(10, 30, 0), result)
        assertTrue(result is LocalTime)
    }

    @Test
    fun `should convert string to OffsetDateTime`() {
        val result = ArgumentConvertor.castArgument("2023-12-25T10:30:00+02:00", "property", OffsetDateTime::class.java)
        assertEquals(OffsetDateTime.of(2023, 12, 25, 10, 30, 0, 0, ZoneOffset.ofHours(2)), result)
        assertTrue(result is OffsetDateTime)
    }

    @Test
    fun `should convert string to ZonedDateTime`() {
        val result = ArgumentConvertor.castArgument("2023-12-25T10:30:00+02:00[Europe/Paris]", "property", ZonedDateTime::class.java)
        assertTrue(result is ZonedDateTime)
        val zonedResult = result as ZonedDateTime
        assertEquals(2023, zonedResult.year)
        assertEquals(12, zonedResult.monthValue)
        assertEquals(25, zonedResult.dayOfMonth)
    }

    @Test
    fun `should convert string to valid enum value`() {
        val result = ArgumentConvertor.castArgument("ACTIVE", "status", Status::class.java)
        assertEquals(Status.ACTIVE, result)
        assertTrue(result is Status)
    }

    @Test
    fun `should convert string to all enum values`() {
        Status.entries.forEach { status ->
            val result = ArgumentConvertor.castArgument(status.name, "status", Status::class.java)
            assertEquals(status, result)
        }
    }

    @Test
    fun `should throw InvalidEnumValueException for invalid enum value`() {
        assertThrows<InvalidEnumValueException> {
            ArgumentConvertor.castArgument("INVALID_STATUS", "status", Status::class.java)
        }
    }

    @Test
    fun `should return original string for String type`() {
        val result = ArgumentConvertor.castArgument("hello world", "property", String::class.java)
        assertEquals("hello world", result)
        assertTrue(result is String)
    }

    @Test
    fun `should return original string for unknown type`() {
        class UnknownClass
        val result = ArgumentConvertor.castArgument("some value", "property", UnknownClass::class.java)
        assertEquals("some value", result)
        assertTrue(result is String)
    }

    @Test
    fun `should return original string when conversion fails`() {
        val result = ArgumentConvertor.castArgument("not-a-number", "property", Int::class.java)
        assertEquals("not-a-number", result)
        assertTrue(result is String)
    }

    @Test
    fun `should return original string for invalid UUID`() {
        val result = ArgumentConvertor.castArgument("invalid-uuid", "property", UUID::class.java)
        assertEquals("invalid-uuid", result)
        assertTrue(result is String)
    }

    @Test
    fun `should return original string for null java type`() {
        val result = ArgumentConvertor.castArgument("test", "property", null)
        assertEquals("test", result)
    }

    @Test
    fun `should handle negative numbers`() {
        assertEquals(-42, ArgumentConvertor.castArgument("-42", "property", Int::class.java))
        assertEquals(-3.14, ArgumentConvertor.castArgument("-3.14", "property", Double::class.java))
        assertEquals(-100L, ArgumentConvertor.castArgument("-100", "property", Long::class.java))
    }

    @Test
    fun `should handle decimal numbers with many decimal places`() {
        val result = ArgumentConvertor.castArgument("123.45678901234567890", "property", BigDecimal::class.java)
        assertEquals(BigDecimal("123.45678901234567890"), result)
    }

    @Test
    fun `should handle empty string for Char conversion`() {
        // This should fail and return the original string
        val result = ArgumentConvertor.castArgument("", "property", Char::class.java)
        assertEquals("", result)
    }

    @Test
    fun `should take first character when converting multi-char string to Char`() {
        val result = ArgumentConvertor.castArgument("ABC", "property", Char::class.java)
        assertEquals('A', result)
    }
}
