package com.sleekydz86.chat.global.bean

import com.fasterxml.jackson.annotation.JsonProperty

data class SearXNGResponse(
    @JsonProperty("query")
    val query: String? = null,

    @JsonProperty("results")
    val results: List<SearchResult>? = null
)
