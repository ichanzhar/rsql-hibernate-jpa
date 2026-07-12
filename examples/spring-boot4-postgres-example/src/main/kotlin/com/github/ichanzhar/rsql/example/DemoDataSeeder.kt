package com.github.ichanzhar.rsql.example

import com.github.ichanzhar.rsql.example.domain.Author
import com.github.ichanzhar.rsql.example.domain.Book
import com.github.ichanzhar.rsql.example.domain.Category
import com.github.ichanzhar.rsql.example.domain.Chapter
import com.github.ichanzhar.rsql.example.domain.Dimensions
import com.github.ichanzhar.rsql.example.domain.Review
import com.github.ichanzhar.rsql.example.repository.BookRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

// Only active for local `bootRun` demos (see README) - not activated by the integration test,
// which seeds its own fixed data set.
@Profile("demo")
@Component
class DemoDataSeeder(private val bookRepository: BookRepository) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (bookRepository.count() > 0) return

        val tolkien = Author(name = "J.R.R. Tolkien", email = "tolkien@example.com")
        val herbert = Author(name = "Frank Herbert", email = "herbert@example.com")

        val fantasy = Category(name = "Fantasy")
        val scifi = Category(name = "Sci-Fi")

        bookRepository.save(
            Book(
                title = "The Hobbit",
                isbn = "9780618260300",
                publicationYear = 1937,
                author = tolkien,
                metadata = """{"genre":"fantasy","pages":310}""",
                dimensions = Dimensions(widthCm = 13.0, heightCm = 20.0, weightGrams = 340),
                tags = mutableSetOf("fantasy", "classic"),
                categories = mutableSetOf(fantasy),
                reviews = mutableSetOf(
                    Review(rating = 5, comment = "A timeless classic", labels = mutableSetOf("editorial")),
                    Review(rating = 4, comment = "Great start to the saga", labels = mutableSetOf("urgent", "community")),
                ),
                chapters = mutableListOf(
                    Chapter(sequence = 1, title = "An Unexpected Party"),
                    Chapter(sequence = 2, title = "Roast Mutton"),
                ),
            )
        )
        bookRepository.save(
            Book(
                title = "Dune",
                isbn = null,
                publicationYear = 1965,
                author = herbert,
                metadata = """{"genre":"scifi","pages":412}""",
                dimensions = Dimensions(widthCm = 15.0, heightCm = 23.0, weightGrams = 480),
                tags = mutableSetOf("scifi", "epic"),
                categories = mutableSetOf(scifi),
                reviews = mutableSetOf(
                    Review(rating = 5, comment = "Genre-defining", labels = mutableSetOf("urgent", "editorial")),
                ),
                chapters = mutableListOf(
                    Chapter(sequence = 1, title = "Prologue"),
                ),
            )
        )
    }
}
