package com.sleekydz86.mcp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class McpServerApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<McpServerApplication>(*args)
        }
    }
}
