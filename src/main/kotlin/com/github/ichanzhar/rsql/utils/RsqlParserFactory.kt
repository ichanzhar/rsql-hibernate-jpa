package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.RsqlOperation
import cz.jirutka.rsql.parser.RSQLParser

object RsqlParserFactory {
	fun instance() : RSQLParser {
		return RSQLParser(RsqlOperation.values().map { it.operator }.toMutableSet())
	}
}