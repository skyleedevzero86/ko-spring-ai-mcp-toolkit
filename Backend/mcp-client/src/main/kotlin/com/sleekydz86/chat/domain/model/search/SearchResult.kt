package com.sleekydz86.chat.domain.model.search

data class SearchResult(
    val title: String? = null,
    val content: String? = null,
    val url: String? = null,
    val score: Double? = null
)
