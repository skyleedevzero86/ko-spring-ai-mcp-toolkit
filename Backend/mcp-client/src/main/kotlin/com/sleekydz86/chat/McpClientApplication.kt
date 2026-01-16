package com.sleekydz86.chat

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class McpClientApplication

fun main(args: Array<String>) {

    val dotenv = Dotenv.configure().ignoreIfMissing().load()

    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    runApplication<McpClientApplication>(*args)
}
