package com.sleekydz86.chat.domain.controller

import com.sleekydz86.chat.domain.application.chat.ChatApplicationService
import com.sleekydz86.chat.global.bean.ChatEntity
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatApplicationService: ChatApplicationService
) {

    @PostMapping("/send")
    fun chat(@Valid @RequestBody chatEntity: ChatEntity) {
        chatApplicationService.streamChat(chatEntity)
    }
}
