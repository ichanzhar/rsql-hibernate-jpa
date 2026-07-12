package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.exception.InvalidDateFormatException
import com.github.ichanzhar.rsql.exception.InvalidEnumValueException
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


object ArgumentConvertor {

	fun castArgument(arg: String, property: String?, javaType: Class<out Any>?): Any {
		try {
			when (javaType) {
				Int::class.java -> return arg.toInt()
				Long::class.java -> return arg.toLong()
				BigInteger::class.java -> return arg.toBigInteger()
				Double::class.java -> return arg.toDouble()
				Float::class.java -> return arg.toFloat()
				BigDecimal::class.java -> return arg.toBigDecimal()
				Char::class.java -> return arg[0]
				Short::class.java -> return arg.toShort()
				Boolean::class.java -> return arg.toBoolean()
				UUID::class.java -> return UUID.fromString(arg)
				Timestamp::class.java, Date::class.java -> return parseDate(arg, property)
				LocalDate::class.java -> return LocalDate.parse(arg)
				LocalDateTime::class.java -> return LocalDateTime.parse(arg)
				LocalTime::class.java -> return LocalTime.parse(arg)
				OffsetDateTime::class.java -> return OffsetDateTime.parse(arg)
				ZonedDateTime::class.java -> return ZonedDateTime.parse(arg)
				else -> {
					if (isEnumClass(javaType)) {
						return getEnumValue(javaType, arg)
					}
					return arg
				}
			}
		} catch (e: Exception) {
			return arg
		}
	}

	private fun isEnumClass(clazz: Class<out Any>?): Boolean {
		return clazz?.isEnum == true
	}

	private fun parseDate(arg: String, property: String?): Date {
		try {
			return Date.from(LocalDateTime.parse(arg).atZone(ZoneId.systemDefault()).toInstant())
		} catch (e: Exception) { }
		try {
			return Date.from(LocalDateTime.parse(arg, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")).atZone(ZoneId.systemDefault()).toInstant())
		} catch (e: Exception) {
			throw InvalidDateFormatException(arg, property)
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun getEnumValue(enumClass: Class<out Any>?, value: String): Enum<*> {
		val enumConstants = enumClass?.enumConstants as Array<out Enum<*>>
		try {
			return enumConstants.first { it.name == value }
		} catch (e: NoSuchElementException) {
			throw InvalidEnumValueException(enumClass, value)
		}
	}


}