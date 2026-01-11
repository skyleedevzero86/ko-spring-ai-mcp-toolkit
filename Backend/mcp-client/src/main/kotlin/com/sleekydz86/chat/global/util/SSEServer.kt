package com.sleekydz86.chat.global.util

import com.sleekydz86.chat.global.enum.SSEMsgType
import org.slf4j.LoggerFactory
import org.springframework.util.CollectionUtils
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

object SSEServer {
    private val log = LoggerFactory.getLogger(SSEServer::class.java)


    private val sseClients = ConcurrentHashMap<String, SseEmitter>()

    fun connect(userId: String): SseEmitter {

        val sseEmitter = SseEmitter(0L)
        sseEmitter.onTimeout(timeoutCallback(userId))
        sseEmitter.onCompletion(completionCallback(userId))
        sseEmitter.onError(errorCallback(userId))

        sseClients[userId] = sseEmitter
        return sseEmitter
    }

    fun close(userId: String) {
        val emitter = sseClients[userId]
        emitter?.complete()
    }

    fun sendMsg(userId: String, message: String, msgType: SSEMsgType) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return
        }

        sseClients[userId]?.let { sseEmitter ->
            sendEmitterMessage(sseEmitter, userId, message, msgType)
        }
    }

    fun sendMsgToAllUsers(message: String) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return
        }

        sseClients.forEach { (userId, sseEmitter) ->
            sendEmitterMessage(sseEmitter, userId, message, SSEMsgType.MESSAGE)
        }
    }

    private fun sendEmitterMessage(
        sseEmitter: SseEmitter,
        userId: String,
        message: String,
        msgType: SSEMsgType
    ) {
        val msgEvent = SseEmitter.event()
            .id(userId)
            .data(message)
            .name(msgType.type)

        try {
            sseEmitter.send(msgEvent)
        } catch (e: IOException) {
            log.error("SSE send message error, userId: {}, error: {}", userId, e.message)
            close(userId)
        }
    }

    fun timeoutCallback(userId: String): Runnable {
        return Runnable {
            log.info("SSE 타임아웃...")
            remove(userId)
        }
    }

    fun completionCallback(userId: String): Runnable {
        return Runnable {
            log.info("SSE 완료...")
            remove(userId)
        }
    }

    fun errorCallback(userId: String): (Throwable) -> Unit {
        return { throwable ->
            log.info("SSE 예외...")
            remove(userId)
        }
    }

    fun remove(userId: String) {
        sseClients.remove(userId)
        log.info("사용자 제거: {}", userId)
    }
}
