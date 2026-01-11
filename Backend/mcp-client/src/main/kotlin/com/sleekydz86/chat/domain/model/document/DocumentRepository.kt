package com.sleekydz86.chat.domain.model.document

import org.springframework.ai.document.Document

interface DocumentRepository {
    fun saveAll(documents: List<Document>)
    fun findSimilarDocuments(question: String): List<Document>
}