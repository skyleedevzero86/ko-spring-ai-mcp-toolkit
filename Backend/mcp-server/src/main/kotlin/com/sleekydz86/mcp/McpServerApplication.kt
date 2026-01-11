package com.sleekydz86.mcp

import com.sleekydz86.mcp.tool.DateTool
import com.sleekydz86.mcp.tool.EmailTool
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class McpServerApplication {

    @Bean
    fun registerMCPTools(dateTool: DateTool, emailTool: EmailTool): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(dateTool, emailTool)
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<McpServerApplication>(*args)
}
