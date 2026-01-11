package com.sleekydz86.chat.domain.model.chat.strategy

import com.sleekydz86.chat.global.enum.ChatMode
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component

@Component
class DirectChatPromptStrategy : PromptStrategy {

    override fun createPrompt(question: String): Prompt {
        return Prompt(question)
    }

    override fun getSupportedMode(): ChatMode {
        return ChatMode.DIRECT
    }
}