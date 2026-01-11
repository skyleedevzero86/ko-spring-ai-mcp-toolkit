package com.sleekydz86.chat.domain.infrastructure.document

import com.sleekydz86.chat.domain.model.document.DocumentSplitter
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TextSplitter
import org.springframework.stereotype.Component

@Component
class CustomDocumentSplitter : TextSplitter(), DocumentSplitter {

    override fun split(documents: List<Document>): List<Document> {
        return documents.flatMap { doc ->
            val text = doc.text ?: return@flatMap emptyList()
            val splitTexts = splitText(text)
            splitTexts.map { splitText ->
                Document(splitText, doc.metadata)
            }
        }
    }

    override fun splitText(text: String): List<String> {
        val splitArray = text.split("\\s*\\R\\s*\\R\\S*".toRegex())
        return splitArray.toList()
    }
}
