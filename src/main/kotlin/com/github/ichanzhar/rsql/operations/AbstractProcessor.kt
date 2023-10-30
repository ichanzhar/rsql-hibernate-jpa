package com.github.ichanzhar.rsql.operations

import jakarta.persistence.criteria.CollectionJoin
import jakarta.persistence.criteria.ListJoin
import jakarta.persistence.criteria.SetJoin
import org.hibernate.metamodel.model.domain.ManagedDomainType
import org.hibernate.query.criteria.JpaRoot
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

abstract class AbstractProcessor(protected val params: Params) : Processor {
	private val log: XLogger = XLoggerFactory.getXLogger(this::class.java)

	fun isRootJoin(): Boolean {
		try {
			if (JpaRoot::class.java.isAssignableFrom(params.root.javaClass)) {
				return MutableCollection::class.java.isAssignableFrom(
					(params.root as JpaRoot<*>).model.getAttribute(params.globalProperty).javaType
				)
			}
		} catch (ex: Exception) {
			log.warn("unexpected error while root element collection: {}", ex.message)
		}
		return false
	}

	fun isSetJoin(): Boolean {
		try {
			if(params.root is SetJoin<*,*>) {
				return Collection::class.java.isAssignableFrom(params.root.get<ManagedDomainType<Any>>(getPropertyNodeName()).javaType)
			}
		} catch (ex: Exception) {
			log.warn("unexpected error while checking singular join collection: {}", ex.message)
		}
		return false
	}

	fun isListJoin(): Boolean {
		try {
			if (ListJoin::class.java.isAssignableFrom(params.root.javaClass))
				return Collection::class.java.isAssignableFrom(params.root.get<ManagedDomainType<Any>>(getPropertyNodeName()).javaType)
		} catch (ex: Exception) {
			log.warn("unexpected error while checking singular join collection: {}", ex.message)
		}
		return false
	}

	fun isCollectionJoin(): Boolean {
		try {
			if (CollectionJoin::class.java.isAssignableFrom(params.root.javaClass)) {
				return MutableCollection::class.java.isAssignableFrom(
					params.root.get<ManagedDomainType<Any>>(getPropertyNodeName()).javaType
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