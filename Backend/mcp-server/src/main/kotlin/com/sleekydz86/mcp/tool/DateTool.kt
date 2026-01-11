package com.sleekydz86.mcp.tool

import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class DateTool {
    private val log = LoggerFactory.getLogger(DateTool::class.java)

    @Tool(description = "현재 시간 가져오기")
    fun getCurrentTime(): String {
        log.info("=================MCP 도구 호출: 현재 시간 가져오기=================")
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        return "현재 시간은 $now 입니다"
    }
}
