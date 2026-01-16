package com.sleekydz86.chat.domain.infrastructure.document

import com.sleekydz86.chat.domain.model.document.DocumentRepository
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.redis.RedisVectorStore
import org.springframework.stereotype.Repository

@Repository
class RedisDocumentRepository(
    private val redisVectorStore: RedisVectorStore
) : DocumentRepository {

    override fun saveAll(documents: List<Document>) {
        redisVectorStore.add(documents)
    }

    override fun findSimilarDocuments(question: String): List<Document> {
        return redisVectorStore.similaritySearch(question)
    }
}
