package com.github.ichanzhar.rsql.example.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn

@Entity
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var rating: Int = 0,

    var comment: String = "",

    // Element collection nested one hop below an association join (book.reviews.labels) -
    // exercises AbstractProcessor's isSetJoin() special-casing.
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "review_labels", joinColumns = [JoinColumn(name = "review_id")])
    @Column(name = "label")
    var labels: MutableSet<String> = mutableSetOf(),
)
