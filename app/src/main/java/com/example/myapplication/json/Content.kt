package com.example.myapplication.json

import com.squareup.moshi.Json

data class Content(
    val id: Int,
    val city: String,
    val category: String,
    @Json(name="article_id") val articleId: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Content

        if (id != other.id) return false
        if (city != other.city) return false
        if (category != other.category) return false
        if (!articleId.contentEquals(other.articleId)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + city.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + articleId.contentHashCode()
        return result
    }
}