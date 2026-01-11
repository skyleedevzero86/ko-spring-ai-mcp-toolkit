package com.sleekydz86.chat.domain.application.chat

import com.sleekydz86.chat.domain.infrastructure.sse.SseEventService
import com.sleekydz86.chat.domain.model.chat.strategy.PromptStrategy
import com.sleekydz86.chat.domain.model.chat.strategy.PromptStrategyFactory
import com.sleekydz86.chat.global.bean.ChatEntity
import com.sleekydz86.chat.global.enum.ChatMode
import com.sleekydz86.chat.global.enum.SSEMsgType
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChatApplicationService(
    private val chatClient: ChatClient,
    private val promptStrategyFactory: PromptStrategyFactory,
    private val sseEventService: SseEventService
) {
    private val log = LoggerFactory.getLogger(ChatApplicationService::class.java)

    fun streamChat(chatEntity: ChatEntity) {
        val userId = chatEntity.currentUserName ?: "anonymous"
        val question = chatEntity.message ?: ""
        val mode = chatEntity.mode ?: ChatMode.DIRECT

        val strategy: PromptStrategy = promptStrategyFactory.getStrategy(mode)
        val prompt: Prompt = strategy.createPrompt(question)

        log.info("【사용자: {}】【{} 모드】로 질문 중입니다.", userId, mode)

        val stream: Flux<String> = chatClient.prompt(prompt).stream().content()
        stream.doOnError { throwable ->
            log.error("【사용자: {}】의 AI 스트림 처리 중 오류 발생: {}", userId, throwable.message, throwable)
            sseEventService.sendMessage(userId, "죄송합니다. 서비스에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", SSEMsgType.FINISH)
            sseEventService.closeConnection(userId)
        }
            .subscribe(
                { content -> sseEventService.sendMessage(userId, content, SSEMsgType.ADD) },
                { error -> log.error("【사용자: {}】의 스트림 구독 최종 실패: {}", userId, error.message) },
                {
                    log.info("【사용자: {}】의 스트림이 성공적으로 종료되었습니다.", userId)
                    sseEventService.sendMessage(userId, "done", SSEMsgType.FINISH)
                    sseEventService.closeConnection(userId)
                }
            )
    }
}
