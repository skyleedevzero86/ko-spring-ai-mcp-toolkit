package com.sleekydz86.chat.global.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ProxyConfig(
    @Value("\${proxy.host:127.0.0.1}") private val proxyHost: String,
    @Value("\${proxy.port:10080}") private val proxyPort: Int
) {
    @PostConstruct
    fun setSystemProxy() {
        System.setProperty("http.proxyHost", proxyHost)
        System.setProperty("http.proxyPort", proxyPort.toString())
        System.setProperty("https.proxyHost", proxyHost)
        System.setProperty("https.proxyPort", proxyPort.toString())

        println("시스템 프록시 구성됨: http://$proxyHost:$proxyPort")
    }
}