package com.github.ichanzhar.rsql.example.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String = "",

    var isbn: String? = null,

    var publicationYear: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var author: Author? = null,

    // Raw jsonb column, filterable via the library's Postgres-only =jsonbeq= operator.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var metadata: String? = null,

    @Embedded
    var dimensions: Dimensions = Dimensions(),

    // Element collection directly on the root entity - queried with no dot-notation at all
    // (e.g. "tags==fiction"), exercising AbstractProcessor.isRootJoin().
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_tags", joinColumns = [JoinColumn(name = "book_id")])
    @Column(name = "tag")
    var tags: MutableSet<String> = mutableSetOf(),

    // One-to-many association whose target itself has an element collection
    // ("reviews.labels==...") - exercises AbstractProcessor.isSetJoin().
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    var reviews: MutableSet<Review> = mutableSetOf(),

    // One-to-many association backed by a List rather than a Set (still a plain nested-field
    // join, e.g. "chapters.title==...").
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    var chapters: MutableList<Chapter> = mutableListOf(),

    // Many-to-many join (e.g. "categories.name==...").
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "book_categories",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
    )
    var categories: MutableSet<Category> = mutableSetOf(),
)
