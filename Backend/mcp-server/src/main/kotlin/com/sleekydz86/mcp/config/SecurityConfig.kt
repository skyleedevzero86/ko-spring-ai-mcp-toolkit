package com.sleekydz86.mcp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { csrf ->
                csrf.disable()
            }
            .authorizeExchange { exchanges ->
                exchanges
                    .anyExchange().permitAll()
            }
            .build()
    }
}
