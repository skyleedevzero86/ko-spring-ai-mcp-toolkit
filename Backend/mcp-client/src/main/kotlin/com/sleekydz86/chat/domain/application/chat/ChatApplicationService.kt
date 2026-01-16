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
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux
import reactor.util.retry.Retry
import java.time.Duration

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
            .retryWhen(
                Retry.backoff(3, Duration.ofSeconds(2))
                    .filter { throwable ->
                        when (throwable) {
                            is WebClientResponseException.TooManyRequests -> {
                                log.warn("【사용자: {}】Rate limit 초과. 재시도 중... (429)", userId)
                                true
                            }
                            is WebClientResponseException -> {
                                val statusCode = throwable.statusCode.value()
                                if (statusCode in 500..599) {
                                    log.warn("【사용자: {}】서버 오류. 재시도 중... ({})", userId, statusCode)
                                    true
                                } else {
                                    false
                                }
                            }
                            else -> false
                        }
                    }
                    .doBeforeRetry { retrySignal ->
                        log.info("【사용자: {}】재시도 {}회차", userId, retrySignal.totalRetries() + 1)
                    }
            )
        
        stream.doOnError { throwable ->
            when (throwable) {
                is WebClientResponseException.TooManyRequests -> {
                    log.error("【사용자: {}】Rate limit 초과로 인한 최종 실패. 잠시 후 다시 시도해주세요.", userId)
                    sseEventService.sendMessage(userId, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", SSEMsgType.FINISH)
                }
                else -> {
                    log.error("【사용자: {}】의 AI 스트림 처리 중 오류 발생: {}", userId, throwable.message, throwable)
                    sseEventService.sendMessage(userId, "죄송합니다. 서비스에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", SSEMsgType.FINISH)
                }
            }
            sseEventService.closeConnection(userId)
        }
            .subscribe(
                { content -> sseEventService.sendMessage(userId, content, SSEMsgType.ADD) },
                { error -> 
                    when (error) {
                        is WebClientResponseException.TooManyRequests -> {
                            log.error("【사용자: {}】Rate limit 초과로 인한 스트림 구독 최종 실패", userId)
                        }
                        else -> {
                            log.error("【사용자: {}】의 스트림 구독 최종 실패: {}", userId, error.message)
                        }
                    }
                },
                {
                    log.info("【사용자: {}】의 스트림이 성공적으로 종료되었습니다.", userId)
                    sseEventService.sendMessage(userId, "done", SSEMsgType.FINISH)
                    sseEventService.closeConnection(userId)
                }
            )
    }
}
