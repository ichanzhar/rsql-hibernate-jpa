package com.github.ichanzhar.rsql.example.web

import com.github.ichanzhar.rsql.JpaRsqlVisitor
import com.github.ichanzhar.rsql.ParserContext
import com.github.ichanzhar.rsql.example.domain.Book
import com.github.ichanzhar.rsql.example.repository.BookRepository
import com.github.ichanzhar.rsql.utils.RsqlParserFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookRepository: BookRepository) {

    @GetMapping
    fun search(@RequestParam(required = false) query: String?): List<Book> {
        if (query.isNullOrBlank()) {
            return bookRepository.findAll()
        }
        val node = RsqlParserFactory.instance(ParserContext.POSTGRESQL).parse(query)
        // distinct=true avoids duplicate rows fanned out by collection joins (reviews,
        // chapters, categories, tags).
        val spec = node.accept(JpaRsqlVisitor<Book>(distinct = true))
        return bookRepository.findAll(spec)
    }
}
