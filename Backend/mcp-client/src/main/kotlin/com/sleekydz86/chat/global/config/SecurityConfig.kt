package com.sleekydz86.chat.global.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val corsConfig: CorsConfig
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers("/sse/**", "/chat/**", "/rag/**")
            }
            .cors { cors ->
                cors.configurationSource(corsConfig.corsConfigurationSource())
            }
            .headers { headers ->
                headers
                    .contentSecurityPolicy { policy ->
                        policy.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline';")
                    }
                    .frameOptions { frameOptions ->
                        frameOptions.deny()
                    }
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/sse/**", "/chat/**", "/rag/**", "/actuator/health").permitAll()
                    .anyRequest().authenticated()
            }

        return http.build()
    }
}