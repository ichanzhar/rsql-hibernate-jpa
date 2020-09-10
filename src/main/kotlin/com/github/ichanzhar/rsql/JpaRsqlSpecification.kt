package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.exception.InvalidDateFormatException
import com.github.ichanzhar.rsql.operations.Params
import com.github.ichanzhar.rsql.operations.ProcessorsFactory.Companion.getProcessor
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import org.apache.commons.lang3.StringUtils
import org.joda.time.format.ISODateTimeFormat
import org.springframework.data.jpa.domain.Specification
import java.util.*
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
		val params = Params(root, builder!!, property!!, globalProperty, args, args[0], javaType!!)
		return getProcessor(operator, params).process()
	}

	private fun isAssociation(root: Root<T>, propertyName: String): Boolean {
		return root.model.getAttribute(propertyName).isAssociation
	}

	private fun containsJoins(property: String): Boolean {
		return StringUtils.contains(property, ".")
	}

	private fun getPath(root: Root<T>, tokenizer: StringTokenizer, token: String): Path<T> {
		var joinToken: String? = token
		var join: Join<*, *> = root.join<Any, Any>(joinToken)
		while (tokenizer.countTokens() >= 2) {
			joinToken = tokenizer.nextToken()
			join = join.join<Any, T>(joinToken)
		}
		return join as Path<T>
	}

	private fun castArguments(root: Path<T>, property: String?): List<Any> {
		val args: MutableList<Any> = ArrayList()
		javaType = root.get<Any>(property).javaType
		for (argument in arguments) {
			if (javaType == Int::class.java) {
				args.add(argument.toInt())
			} else if (javaType == Long::class.java) {
				args.add(argument.toLong())
			} else if (javaType == Boolean::class.java) {
				args.add(java.lang.Boolean.valueOf(argument))
			} else if (isDate()) {
				try {
					val dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(argument)
					args.add(dateTime.toDate())
				} catch (e: IllegalArgumentException) {
					throw InvalidDateFormatException(argument, property)
				}
			} else {
				args.add(argument)
			}
		}
		return args
	}

	private fun isDate(): Boolean {
		return Date::class.java.isAssignableFrom(javaType)
	}
}