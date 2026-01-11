package com.sleekydz86.chat.domain.infrastructure.search

import cn.hutool.json.JSONUtil
import com.sleekydz86.chat.domain.model.search.SearchResult
import com.sleekydz86.chat.domain.model.search.SearchService
import com.sleekydz86.chat.global.bean.SearXNGResponse
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import kotlin.collections.sortedByDescending

@Service
class SearXngSearchService(
    private val okHttpClient: OkHttpClient,
    @Value("\${internet.websearch.searxng.url}") private val searXngUrl: String,
    @Value("\${internet.websearch.searxng.counts}") private val searchResultCount: Int
) : SearchService {

    private val log = LoggerFactory.getLogger(SearXngSearchService::class.java)

    override fun search(query: String): List<SearchResult> {
        val url = HttpUrl.get(searXngUrl)
            .newBuilder()
            .addQueryParameter("q", query)
            .addQueryParameter("format", "json")
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        log.info("SearXNG에 요청 전송 중: {}", url)

        return try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: "응답 본문을 가져올 수 없음"
                    log.error("SearXNG 요청 실패. 상태 코드: {}, URL: {}, 응답 본문: {}", response.code, url, errorBody)
                    throw RuntimeException("SearXNG 요청 실패. 상태 코드: ${response.code}, URL: $url, 응답 본문: $errorBody")
                }

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    log.debug("SearXNG 응답 내용: {}", responseBody)

                    val searXNGResponse = JSONUtil.toBean(responseBody, SearXNGResponse::class.java)
                    if (searXNGResponse != null && searXNGResponse.results != null) {
                        processSearchResults(searXNGResponse.results)
                    } else {
                        log.warn("SearXNG가 반환한 JSON을 파싱할 수 없거나 결과가 비어있음. 응답: {}", responseBody)
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
        } catch (e: IOException) {
            log.error("SearXNG 요청 중 네트워크 IO 예외 발생, URL: {}", url, e)
            throw RuntimeException("SearXNG 요청 중 네트워크 IO 예외 발생", e)
        }
    }

    private fun processSearchResults(results: List<BeanSearchResult>): List<SearchResult> {
        if (results.isEmpty()) {
            return emptyList()
        }

        return results
            .sortedByDescending { it.score ?: 0.0 }
            .take(searchResultCount)
            .map { convertToDomainResult(it) }
    }

    private fun convertToDomainResult(beanResult: BeanSearchResult): SearchResult {
        return SearchResult(
            title = beanResult.title,
            content = beanResult.content,
            url = beanResult.url,
            score = beanResult.score
        )
    }
}
