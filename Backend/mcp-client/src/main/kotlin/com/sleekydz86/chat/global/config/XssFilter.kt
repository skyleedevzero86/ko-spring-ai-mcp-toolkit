package com.sleekydz86.chat.global.config


import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.owasp.encoder.Encode
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
@Order(1)
class XssFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrappedRequest = ContentCachingRequestWrapper(request, 65536)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            val contentType = response.contentType
            if (contentType != null && !contentType.contains("application/json") && !contentType.contains("text/event-stream")) {
                val responseBody = String(wrappedResponse.contentAsByteArray)
                if (responseBody.isNotEmpty()) {
                    val sanitized = sanitizeInput(responseBody)
                    wrappedResponse.resetBuffer()
                    wrappedResponse.setContentLength(sanitized.length)
                    wrappedResponse.writer.write(sanitized)
                }
            }
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun sanitizeInput(input: String): String {
        return Encode.forHtml(input)
            .replace("<script", "&lt;script")
            .replace("</script>", "&lt;/script&gt;")
            .replace("javascript:", "")
            .replace("onerror=", "")
            .replace("onload=", "")
    }
}
