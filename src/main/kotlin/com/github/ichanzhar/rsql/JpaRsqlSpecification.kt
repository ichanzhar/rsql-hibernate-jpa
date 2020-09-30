package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.exception.InvalidDateFormatException
import com.github.ichanzhar.rsql.exception.InvalidEnumValueException
import com.github.ichanzhar.rsql.operations.Params
import com.github.ichanzhar.rsql.operations.ProcessorsFactory.Companion.getProcessor
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import org.apache.commons.lang3.StringUtils
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat
import org.springframework.data.jpa.domain.Specification
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.*
import java.util.*
import java.util.stream.Collectors
import javax.persistence.criteria.*


class JpaRsqlSpecification<T>(
	private val globalProperty: String,
	private val operator: ComparisonOperator,
	private val arguments: List<String>
) : Specification<T> {
	private var javaType: Class<out Any>? = null
	override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate {
		query.distinct(true)
		if (containsJoins(globalProperty)) {
			val tokenizer = StringTokenizer(globalProperty, ".")
			val token = tokenizer.nextToken()
			if (isAssociation(root, token)) {
				val path = getPath(root, tokenizer, token)
				return toPredicate(path, builder, tokenizer.nextToken())
			}
		}
		return toPredicate(root, builder, globalProperty)
	}

	private fun toPredicate(root: Path<T>, builder: CriteriaBuilder?, property: String?): Predicate {
		val args = castArguments(root, property)
		val params = Params(root, builder!!, property!!, globalProperty, args, args[0], javaType)
		return getProcessor(operator, params).process()
	}

	private fun isAssociation(root: Root<T>, propertyName: String): Boolean {
		return root.model.getAttribute(propertyName).isAssociation || root.model.getAttribute(propertyName).persistentAttributeType.name == "EMBEDDED"
	}

	private fun containsJoins(property: String): Boolean {
		return StringUtils.contains(property, ".")
	}

	private fun getPath(root: Root<T>, tokenizer: StringTokenizer, token: String): Path<T> {
		var joinToken: String? = token
		var join: Join<*, T> = root.join<Any, T>(joinToken)
		while (tokenizer.countTokens() >= 2) {
			joinToken = tokenizer.nextToken()
			join = join.join<Any, T>(joinToken)
		}
		return join
	}

	private fun castArguments(root: Path<T>, property: String?): List<Any> {
		this.javaType = getPropertyJavaType(root.get<Any>(property).javaType)
		return arguments.stream().map<Any> { arg: String ->
			when (javaType) {
				Int::class.java -> return@map arg.toInt()
				Long::class.java -> return@map arg.toLong()
				BigInteger::class.java -> return@map arg.toBigInteger()
				Double::class.java -> return@map arg.toDouble()
				Float::class.java -> return@map arg.toFloat()
				BigDecimal::class.java -> return@map arg.toBigDecimal()
				Char::class.java -> return@map arg[0]
				Short::class.java -> return@map arg.toShort()
				Boolean::class.java -> return@map arg.toBoolean()
				UUID::class.java -> return@map UUID.fromString(arg)
				Timestamp::class.java, Date::class.java -> return@map parseDate(arg, property)
				LocalDate::class.java -> return@map LocalDate.parse(arg)
				LocalDateTime::class.java -> return@map LocalDateTime.parse(arg)
				LocalTime::class.java -> return@map LocalTime.parse(arg)
				OffsetDateTime::class.java -> return@map OffsetDateTime.parse(arg)
				ZonedDateTime::class.java -> return@map ZonedDateTime.parse(arg)
				else -> {
					if (isEnumClass(javaType)) {
						return@map getEnumValue(javaType, arg)
					}
					return@map arg
				}
			}
		}.collect(Collectors.toList())
	}

	private fun isEnumClass(clazz: Class<out Any>?): Boolean {
		return clazz?.isEnum == true
	}

	private fun parseDate(arg: String, property: String?): Date {
		try {
			return ISODateTimeFormat.dateTimeParser().parseDateTime(arg).toDate()
		} catch (e: Exception) { }
		try {
			return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").parseDateTime(arg).toDate()
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


	private fun getPropertyJavaType(propertyJavaType: Class<out Any>?): Class<out Any>? {
		return typesWrapper.getOrDefault(propertyJavaType?.name, propertyJavaType)
	}

	private val typesWrapper = mutableMapOf<String, Class<out Any>>()

	init {
		typesWrapper.put("java.lang.Boolean", Boolean::class.java)
		typesWrapper.put("boolean", Boolean::class.java)
		typesWrapper.put("byte", Byte::class.java)
		typesWrapper.put("char", Character::class.java)
		typesWrapper.put("double", Double::class.java)
		typesWrapper.put("float", Float::class.java)
		typesWrapper.put("int", Integer::class.java)
		typesWrapper.put("long", Long::class.java)
		typesWrapper.put("short", Short::class.java)
	}


}