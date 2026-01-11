package com.sleekydz86.chat.domain.controller

import com.sleekydz86.chat.domain.model.search.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class InternetController(
    private val searchService: SearchService
) {
    @GetMapping("/test")
    fun test(@RequestParam query: String): Any {
        return searchService.search(query)
    }
}
