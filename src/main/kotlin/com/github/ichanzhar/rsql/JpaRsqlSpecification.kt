package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.operations.Params
import com.github.ichanzhar.rsql.operations.ProcessorsFactory.Companion.getProcessor
import com.github.ichanzhar.rsql.utils.ArgumentConvertor
import com.github.ichanzhar.rsql.utils.JavaTypeUtil
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.domain.Specification
import java.util.*
import javax.persistence.criteria.*


class JpaRsqlSpecification<T>(
	private val globalProperty: String,
	private val operator: ComparisonOperator,
	private val arguments: List<String>
) : Specification<T> {

	override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate {
		//query.distinct(true)
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
		val params = Params(root, builder!!, property!!, globalProperty, args, args[0])
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
		var join: Join<*, T> = root.join<Any, T>(joinToken, JoinType.LEFT)
		while (tokenizer.countTokens() >= 2) {
			joinToken = tokenizer.nextToken()
			join = join.join<Any, T>(joinToken, JoinType.LEFT)
		}
		return join
	}

	private fun castArguments(root: Path<T>, property: String?): List<Any> {
		val javaType = JavaTypeUtil.getPropertyJavaType(root.get<Any>(property).javaType)
		return arguments.map { ArgumentConvertor.castArgument(it, property, javaType) }.toList()
	}

}