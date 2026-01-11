package com.sleekydz86.chat.domain.model.chat.strategy

import com.sleekydz86.chat.domain.model.search.SearchService
import com.sleekydz86.chat.global.enum.ChatMode
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Component


@Component
class InternetSearchPromptStrategy(
    private val searchService: SearchService
) : PromptStrategy {

    private val log = LoggerFactory.getLogger(InternetSearchPromptStrategy::class.java)

    private val INTERNET_SEARCH_PROMPT_TEMPLATE = """
            당신은 실시간 네트워크 검색 능력을 가진 지능형 어시스턴트입니다. 아래 제공된 최신 네트워크 검색 결과를 기반으로 사용자의 질문에 답변하세요.
            규칙:
            1. 모든 검색 결과를 종합적으로 분석하여 사용자에게 포괄적이고 정확하며 일관된 답변을 제공하세요.
            2. 답변에서 "검색 결과에 따르면..."을 직접 인용하지 말고 자연스럽게 언어를 구성하세요.
            3. 검색 결과가 충분한 정보를 제공하지 못했다면, 솔직하게 사용자에게 알려주세요: "현재 검색 결과로는 귀하의 질문에 대한 정확한 정보를 찾을 수 없습니다."
            4. 답변은 간결하고 명확하며 핵심을 짚어야 합니다.

            【네트워크 검색 결과】
            {context}
                        
            【사용자 질문】
            {question}
            """.trimIndent()

    override fun createPrompt(question: String): Prompt {
        val searchResults = searchService.search(question)
        val context = if (searchResults.isNotEmpty()) {
            searchResults.joinToString("\n\n---\n\n") { result ->
                "【출처 제목】: ${result.title}\n【내용 요약】: ${result.content}\n【링크】: ${result.url}"
            }
        } else {
            "유효한 네트워크 검색 결과를 가져올 수 없습니다."
        }

        val promptContent = INTERNET_SEARCH_PROMPT_TEMPLATE
            .replace("{context}", context)
            .replace("{question}", question)

        return Prompt(promptContent)
    }

    override fun getSupportedMode(): ChatMode {
        return ChatMode.INTERNET_SEARCH
    }
}
