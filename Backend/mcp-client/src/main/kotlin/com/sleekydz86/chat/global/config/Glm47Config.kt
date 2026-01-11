package com.sleekydz86.chat.global.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Glm47Config {

    @Bean
    @ConditionalOnProperty(name = ["spring.ai.openai.api-key"])
    fun chatClient(
        chatClientBuilder: ChatClient.Builder,
        tools: ToolCallbackProvider,
        chatMemory: ChatMemory
    ): ChatClient {
        return chatClientBuilder
            .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
            .defaultToolCallbacks(tools)
            .build()
    }
}