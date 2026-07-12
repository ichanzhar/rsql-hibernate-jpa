package com.github.ichanzhar.rsql.example.domain

import jakarta.persistence.Embeddable

@Embeddable
class Dimensions(
    var widthCm: Double = 0.0,
    var heightCm: Double = 0.0,
    var weightGrams: Int = 0,
)
