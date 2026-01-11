package com.sleekydz86.chat.domain.model.chat.strategy

import com.sleekydz86.chat.domain.model.document.DocumentRepository
import com.sleekydz86.chat.global.enum.ChatMode
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.document.Document
import org.springframework.stereotype.Component

@Component
class KnowledgeBasePromptStrategy(
    private val documentRepository: DocumentRepository
) : PromptStrategy {

    private val log = LoggerFactory.getLogger(KnowledgeBasePromptStrategy::class.java)

    private val RAG_PROMPT_TEMPLATE = """
            아래 제공된 컨텍스트 지식베이스 내용을 기반으로 사용자의 질문에 답변하세요.
            규칙:
            1. 답변 시 컨텍스트 정보를 최대한 활용하되, 답변에서 "컨텍스트에 따르면" 또는 "지식베이스에 따르면" 등의 표현을 직접 언급하지 마세요.
            2. 컨텍스트에 질문에 답변하기에 충분한 정보가 없다면, 명확히 알려주세요: "현재 지식으로는 이 질문에 답변할 수 없습니다."
            3. 답변은 직접적이고 명확하며 관련성 있어야 합니다.

            【컨텍스트】
            {context}
                        
            【질문】
            {question}
            """.trimIndent()

    override fun createPrompt(question: String): Prompt {
        val relatedDocs = documentRepository.findSimilarDocuments(question)
        val context = if (relatedDocs.isNotEmpty()) {
            relatedDocs.joinToString("\n---\n") { it.text }
        } else {
            "관련 지식베이스 정보를 찾을 수 없습니다."
        }

        val promptContent = RAG_PROMPT_TEMPLATE
            .replace("{context}", context)
            .replace("{question}", question)

        return Prompt(promptContent)
    }

    override fun getSupportedMode(): ChatMode {
        return ChatMode.KNOWLEDGE_BASE
    }
}