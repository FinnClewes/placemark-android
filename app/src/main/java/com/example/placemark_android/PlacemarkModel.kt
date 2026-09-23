package com.example.placemark_android

/**
 * Data class representing a single Placemark item.
 * Kotlin automatically generates toString(), equals(), hashCode(), and copy().
 */
data class PlacemarkModel(
    var id: Long = 0L,
    var title: String = "",
    var description: String = "",
    var x: Float = 0F,
    var y: Float = 0F

    // TODO add more here
)