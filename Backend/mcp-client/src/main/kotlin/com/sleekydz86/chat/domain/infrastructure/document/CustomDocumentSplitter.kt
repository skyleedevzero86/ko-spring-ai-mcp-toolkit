package com.sleekydz86.chat.domain.infrastructure.document

import com.sleekydz86.chat.domain.model.document.DocumentSplitter
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TextSplitter
import org.springframework.stereotype.Component

@Component
class CustomDocumentSplitter : TextSplitter(), DocumentSplitter {

    override fun split(documents: List<Document>): List<Document> {
        return documents.flatMap { doc ->
            val splitTexts = splitText(doc.text)
            splitTexts.map { text ->
                Document(text, doc.metadata)
            }
        }
    }

    override fun splitText(text: String): List<String> {
        val splitArray = text.split("\\s*\\R\\s*\\R\\S*".toRegex())
        return splitArray.toList()
    }
}
