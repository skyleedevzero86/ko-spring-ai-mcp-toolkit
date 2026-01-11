package com.sleekydz86.chat.domain.model.chat.strategy

import com.sleekydz86.chat.global.enum.ChatMode
import org.springframework.ai.chat.prompt.Prompt

interface PromptStrategy {
    fun createPrompt(question: String): Prompt
    fun getSupportedMode(): ChatMode
}
