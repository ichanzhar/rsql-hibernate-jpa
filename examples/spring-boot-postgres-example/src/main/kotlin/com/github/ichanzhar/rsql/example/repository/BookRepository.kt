package com.github.ichanzhar.rsql.example.repository

import com.github.ichanzhar.rsql.example.domain.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface BookRepository : JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>
