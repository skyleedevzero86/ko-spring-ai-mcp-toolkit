package com.sleekydz86.chat.domain.model.document

import org.springframework.ai.document.Document

interface DocumentSplitter {
    fun split(documents: List<Document>): List<Document>
}