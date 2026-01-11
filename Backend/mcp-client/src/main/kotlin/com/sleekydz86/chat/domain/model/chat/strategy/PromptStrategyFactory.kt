package com.sleekydz86.chat.domain.model.chat.strategy

import com.sleekydz86.chat.global.enum.ChatMode
import org.springframework.stereotype.Component
@Component
class PromptStrategyFactory(
    strategies: List<PromptStrategy>
) {
    private val strategyMap: Map<ChatMode, PromptStrategy> = strategies.associateBy { it.getSupportedMode() }


    fun getStrategy(mode: ChatMode): PromptStrategy {
        return strategyMap[mode] ?: strategyMap[ChatMode.DIRECT]!!
    }
}
