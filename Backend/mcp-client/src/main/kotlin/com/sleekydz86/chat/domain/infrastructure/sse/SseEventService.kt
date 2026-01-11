package com.sleekydz86.chat.domain.infrastructure.sse

import com.sleekydz86.chat.global.enum.SSEMsgType
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
interface SseEventService {
    fun connect(userId: String): SseEmitter
    fun sendMessage(userId: String, message: String, msgType: SSEMsgType)
    fun closeConnection(userId: String)
}