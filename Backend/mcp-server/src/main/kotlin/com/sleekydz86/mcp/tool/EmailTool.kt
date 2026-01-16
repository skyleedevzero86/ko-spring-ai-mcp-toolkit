package com.sleekydz86.mcp.tool

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class EmailTool(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val from: String
) {
    private val log = LoggerFactory.getLogger(EmailTool::class.java)

    data class EmailRequest(
        @ToolParam(description = "수신자 이메일 주소")
        val email: String,

        @ToolParam(description = "이메일 제목/주제")
        val subject: String,

        @ToolParam(description = "이메일 메시지/본문 내용")
        val message: String,

        @ToolParam(description = "이메일 내용 타입, 1은 HTML 형식, 2는 일반 텍스트 형식")
        val contentType: Int
    )

    @Tool(description = "지정된 이메일 주소로 이메일 정보 전송, email은 수신자 이메일 주소, subject는 이메일 제목, message는 이메일 내용")
    fun sendEmail(emailRequest: EmailRequest): String {
        log.info("=================MCP 도구 호출: sendEmail=================")

        try {
            val mimeMessage = mailSender.createMimeMessage()
            val mimeMessageHelper = MimeMessageHelper(mimeMessage)

            mimeMessageHelper.setFrom(from)
            mimeMessageHelper.setTo(emailRequest.email)
            mimeMessageHelper.setSubject(emailRequest.subject)

            when (emailRequest.contentType) {
                1 -> mimeMessageHelper.setText(convertMarkdownToHtml(emailRequest.message), true)
                2 -> mimeMessageHelper.setText(emailRequest.message, true)
                else -> mimeMessageHelper.setText(emailRequest.message)
            }

            mailSender.send(mimeMessage)
        } catch (e: MessagingException) {
            log.error("이메일 전송 실패", e)
            return "이메일 전송 실패: ${e.message}"
        }

        return "이메일 전송 성공"
    }

    fun convertMarkdownToHtml(markdownStr: String): String {
        val dataSet = MutableDataSet()
        val parser = Parser.builder(dataSet).build()
        val htmlRenderer = HtmlRenderer.builder(dataSet).build()
        return htmlRenderer.render(parser.parse(markdownStr))
    }
}
