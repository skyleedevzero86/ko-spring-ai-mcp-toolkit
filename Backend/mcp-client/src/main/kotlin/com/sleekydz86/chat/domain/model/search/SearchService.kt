package com.sleekydz86.chat.domain.model.search

interface SearchService {
    fun search(query: String): List<SearchResult>
}