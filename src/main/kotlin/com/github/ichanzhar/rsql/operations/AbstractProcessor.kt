package com.github.ichanzhar.rsql.operations

import org.hibernate.query.criteria.internal.path.RootImpl
import org.hibernate.query.criteria.internal.path.SetAttributeJoin
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory
import javax.persistence.metamodel.Attribute

abstract class AbstractProcessor(protected val params: Params) : Processor {
	private val log: XLogger = XLoggerFactory.getXLogger(this::class.java)

	fun isRootJoin(): Boolean {
		try {
			if (RootImpl::class.java.isAssignableFrom(params.root.javaClass)) {
				return MutableCollection::class.java.isAssignableFrom(
					(params.root as RootImpl<*>).entityType.getAttribute(params.globalProperty).javaType
				)
			}
		} catch (ex: Exception) {
			log.warn("unexpected error while root element collection: {}", ex.message)
		}
		return false
	}

	fun isSingularJoin(): Boolean {
		try {
			if (SingularAttributeJoin::class.java.isAssignableFrom(params.root.javaClass))
				return Collection::class.java.isAssignableFrom(params.root.get<Attribute<Any, Any>>(getPropertyNodeName()).javaType)
		} catch (ex: Exception) {
			log.warn("unexpected error while checking singular join collection: {}", ex.message)
		}
		return false
	}


	fun isSetJoin(): Boolean {
		try {
			if (SetAttributeJoin::class.java.isAssignableFrom(params.root.javaClass)) {
				return MutableCollection::class.java.isAssignableFrom(
					params.root.get<Attribute<Any, Any>>(getPropertyNodeName()).javaType
				)
			}
		} catch (ex: Exception) {
			log.warn("unexpected error while checking set element collection: {}", ex.message)
		}
		return false
	}

	fun isLikeExpression(): Boolean {
		return if (params.argument is String) {
			(params.argument as String).startsWith("*") || (params.argument as String).endsWith("*")
		} else false
	}

	fun getFormattedLikePattern(): String {
		return params.argument.toString().replace('*', '%')
	}

	private fun getPropertyNodeName(): String =
		params.globalProperty.substring(params.globalProperty.lastIndexOf(".") + 1)
}