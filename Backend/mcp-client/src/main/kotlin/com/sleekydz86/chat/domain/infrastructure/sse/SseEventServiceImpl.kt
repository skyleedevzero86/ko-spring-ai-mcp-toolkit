package com.sleekydz86.chat.domain.infrastructure.sse

import com.sleekydz86.chat.global.enum.SSEMsgType
import com.sleekydz86.chat.global.util.SSEServer
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class SseEventServiceImpl : SseEventService {

    override fun connect(userId: String): SseEmitter {
        return SSEServer.connect(userId)
    }

    override fun sendMessage(userId: String, message: String, msgType: SSEMsgType) {
        SSEServer.sendMsg(userId, message, msgType)
    }

    override fun closeConnection(userId: String) {
        SSEServer.close(userId)
    }
}