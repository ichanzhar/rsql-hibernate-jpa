package com.github.ichanzhar.rsql.utils

object JavaTypeUtil {

	private val typesWrapper = mutableMapOf<String, Class<out Any>>()

	init {
		typesWrapper["java.lang.Boolean"] = Boolean::class.java
		typesWrapper["boolean"] = Boolean::class.java
		typesWrapper["byte"] = Byte::class.java
		typesWrapper["char"] = Character::class.java
		typesWrapper["double"] = Double::class.java
		typesWrapper["float"] = Float::class.java
		typesWrapper["int"] = Integer::class.java
		typesWrapper["long"] = Long::class.java
		typesWrapper["short"] = Short::class.java
	}

	fun getPropertyJavaType(propertyJavaType: Class<out Any>?): Class<out Any>? {
		return typesWrapper.getOrDefault(propertyJavaType?.name, propertyJavaType)
	}
}