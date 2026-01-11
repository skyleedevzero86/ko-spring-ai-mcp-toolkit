package com.sleekydz86.chat.global.bean

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchResult(
    @JsonProperty("title")
    val title: String? = null,

    @JsonProperty("content")
    val content: String? = null,

    @JsonProperty("url")
    val url: String? = null,

    @JsonProperty("score")
    val score: Double? = null
)
