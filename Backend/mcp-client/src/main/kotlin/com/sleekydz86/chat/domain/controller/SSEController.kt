package com.sleekydz86.chat.domain.controller

import com.sleekydz86.chat.domain.infrastructure.sse.SseEventService
import com.sleekydz86.chat.global.enum.SSEMsgType
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/sse")
class SSEController(
    private val sseEventService: SseEventService
) {
    @GetMapping(path = ["/connect"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(@RequestParam userId: String): SseEmitter {
        return sseEventService.connect(userId)
    }

    @GetMapping("/sendMessage")
    fun sendMessage(
        @RequestParam userId: String,
        @RequestParam message: String
    ): Map<String, String> {
        sseEventService.sendMessage(userId, message, SSEMsgType.MESSAGE)
        return mapOf("status" to "OK")
    }

    @GetMapping("/sendMessageAll")
    fun sendMessageAll(@RequestParam message: String): Map<String, String> {

        return mapOf("status" to "OK")
    }
}
