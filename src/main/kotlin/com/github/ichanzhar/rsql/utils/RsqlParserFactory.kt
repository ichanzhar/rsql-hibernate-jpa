package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.ParserContext
import com.github.ichanzhar.rsql.RsqlOperation
import cz.jirutka.rsql.parser.RSQLParser

object RsqlParserFactory {
	fun instance(context: ParserContext? = null) : RSQLParser {
		val activeContexts = mutableSetOf(ParserContext.COMMON)
		if(context != null) {
			activeContexts.add(context)
		}
		return RSQLParser(RsqlOperation.values().filter { activeContexts.contains(it.context) }.map { it.operator }.toMutableSet())
	}
}