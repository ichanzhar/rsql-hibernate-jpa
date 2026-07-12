package com.github.ichanzhar.rsql.exception

class InvalidEnumValueException(javaType: Class<out Any>?, arg: String) : RuntimeException("can't find '$arg' value in ${javaType?.simpleName} enum")