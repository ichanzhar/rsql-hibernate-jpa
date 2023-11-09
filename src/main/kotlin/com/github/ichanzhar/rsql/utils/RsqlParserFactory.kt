package com.github.ichanzhar.rsql.utils

import com.github.ichanzhar.rsql.ParserContext
import com.github.ichanzhar.rsql.RsqlOperation
import com.github.ichanzhar.rsql.utils.OperationRegistryFactory.operations
import cz.jirutka.rsql.parser.RSQLParser

object RsqlParserFactory {
    /**
     * @param context by default parser is initialized with operations [ParserContext.COMMON] by [RsqlOperation]
     * If you use [ParserContext.POSTGRESQL] as parameter you also add our realization [RsqlOperation.JSON_EQ] and [RsqlOperation.JSONB_EQ]
     * Please don't use [context] if you want to add custom realization of this operators
     * @see OperationRegistryFactory
     */
    fun instance(context: ParserContext? = null): RSQLParser {
        if (context == ParserContext.POSTGRESQL) {
            OperationRegistryFactory.addDefaultPostgresOperation()
        }
        return RSQLParser(operations)
    }
}
