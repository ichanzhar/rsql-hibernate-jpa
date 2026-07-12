package com.github.ichanzhar.rsql.example.web

import com.github.ichanzhar.rsql.example.domain.Author
import com.github.ichanzhar.rsql.example.domain.Book
import com.github.ichanzhar.rsql.example.domain.Category
import com.github.ichanzhar.rsql.example.domain.Chapter
import com.github.ichanzhar.rsql.example.domain.Dimensions
import com.github.ichanzhar.rsql.example.domain.Review
import com.github.ichanzhar.rsql.example.repository.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer = PostgreSQLContainer("postgres:16-alpine")
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var bookRepository: BookRepository

    @BeforeEach
    fun seed() {
        bookRepository.deleteAll()

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
                    Review(rating = 4, comment = "Great start to the saga", labels = mutableSetOf("community")),
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

    @Test
    fun `filters by association join path`() {
        mockMvc.perform(get("/books").param("query", "author.name==*Tolkien*"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("The Hobbit"))
    }

    @Test
    fun `filters by core scalar operator`() {
        mockMvc.perform(get("/books").param("query", "publicationYear=gt=1950"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Dune"))
    }

    @Test
    fun `filters by jsonb equality operator`() {
        mockMvc.perform(get("/books").param("query", "metadata=jsonbeq=genre|scifi"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Dune"))
    }

    @Test
    fun `filters by root-level element collection`() {
        // Book.tags is an @ElementCollection with no association in between - exercises
        // AbstractProcessor.isRootJoin().
        mockMvc.perform(get("/books").param("query", "tags==classic"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("The Hobbit"))
    }

    @Test
    fun `filters by nested field on a one-to-many set association`() {
        // Book.reviews is a plain @OneToMany Set<Review> - ordinary nested-field join.
        mockMvc.perform(get("/books").param("query", "reviews.rating==5"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
    }

    @Test
    fun `filters by element collection nested below a one-to-many association`() {
        // Review.labels is an @ElementCollection reached through Book.reviews - exercises
        // AbstractProcessor.isSetJoin() (join, then a further collection-valued attribute).
        mockMvc.perform(get("/books").param("query", "reviews.labels==urgent"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Dune"))
    }

    @Test
    fun `filters by nested field on a one-to-many list association`() {
        // Book.chapters is a List<Chapter> rather than a Set.
        mockMvc.perform(get("/books").param("query", "chapters.title==Prologue"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Dune"))
    }

    @Test
    fun `filters by many-to-many join`() {
        mockMvc.perform(get("/books").param("query", "categories.name==Fantasy"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("The Hobbit"))
    }

    @Test
    fun `filters by embedded value object field`() {
        mockMvc.perform(get("/books").param("query", "dimensions.weightGrams=gt=400"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Dune"))
    }

    @Test
    fun `combines a join and a collection filter with logical AND`() {
        mockMvc.perform(get("/books").param("query", "author.name==*Tolkien*;reviews.labels==editorial"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("The Hobbit"))
    }
}
