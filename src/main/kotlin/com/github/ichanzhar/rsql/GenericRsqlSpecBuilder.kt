package com.github.ichanzhar.rsql

import cz.jirutka.rsql.parser.ast.ComparisonNode
import cz.jirutka.rsql.parser.ast.LogicalNode
import cz.jirutka.rsql.parser.ast.LogicalOperator
import cz.jirutka.rsql.parser.ast.Node
import org.springframework.data.jpa.domain.Specification

class GenericRsqlSpecBuilder<E>(val distinct: Boolean) {

    fun createSpecification(node: Node): Specification<E>? {
        if (node is LogicalNode) {
            return createSpecification(node)
        }
        if (node is ComparisonNode) {
            return createSpecification(node)!!
        }
        return null
    }

    fun createSpecification(logicalNode: LogicalNode): Specification<E> {
        val specs: MutableList<Specification<E>> = logicalNode.children
            .mapNotNull { createSpecification(it) }
            .toMutableList()


        var result: Specification<E> = specs[0]
        if (logicalNode.operator == LogicalOperator.AND) {
            for (i in 1 until specs.size) {
                result = result.and(specs[i])
            }
        } else if (logicalNode.operator == LogicalOperator.OR) {
            for (i in 1 until specs.size) {
                result = result.or(specs[i])
            }
        }
        return result
    }

    fun createSpecification(comparisonNode: ComparisonNode): Specification<E>? {
        return Specification<E> { root, query, criteriaBuilder ->
            JpaRsqlSpecification<E>(
                comparisonNode.selector,
                comparisonNode.operator,
                comparisonNode.arguments,
                distinct
            ).toPredicate(root, query, criteriaBuilder)
        }
    }

}
