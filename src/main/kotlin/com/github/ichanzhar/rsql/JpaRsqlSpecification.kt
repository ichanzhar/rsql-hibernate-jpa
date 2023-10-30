package com.github.ichanzhar.rsql

import com.github.ichanzhar.rsql.operations.Params
import com.github.ichanzhar.rsql.operations.ProcessorsFactory.getProcessor
import com.github.ichanzhar.rsql.utils.ArgumentConvertor
import com.github.ichanzhar.rsql.utils.JavaTypeUtil
import cz.jirutka.rsql.parser.ast.ComparisonOperator
import jakarta.persistence.criteria.*
import jakarta.persistence.metamodel.Attribute
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.criteria.JpaRoot
import org.springframework.data.jpa.domain.Specification
import java.util.*


class JpaRsqlSpecification<T>(
	private val globalProperty: String,
	private val operator: ComparisonOperator,
	private val arguments: List<String>,
	private val distinct: Boolean,
) : Specification<T> {

	override fun toPredicate(root: Root<T>, query: CriteriaQuery<*>, builder: CriteriaBuilder): Predicate {
		if(distinct) query.distinct(true)
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
		return root.model.getAttribute(propertyName).isAssociation ||
				root.model.getAttribute(propertyName).persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED
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
		val argumentJavaType = root.get<Any>(property).model.bindableJavaType
		val propertyJavaType = JavaTypeUtil.getPropertyJavaType(argumentJavaType)
		return arguments.map { ArgumentConvertor.castArgument(it, property, propertyJavaType) }.toList()
	}

//	private fun getArgumentJavaType(root: Path<T>, property: String?): Class<out Any>? {
//		var jt = root.get<Any>(property).javaType
//		try {
//			val field = (root as JpaRoot).model.javaType.declaredFields.first { it.name == property }
//			if(Collection::class.java.isAssignableFrom(field.type)) {
//				val genericType = field.genericType as java.lang.reflect.ParameterizedType
//				jt  = Class.forName(genericType.actualTypeArguments[0].typeName)
//			}
//		} catch (e: Throwable) {
//			//todo handle more cases
//		}
//		return jt
//	}

}