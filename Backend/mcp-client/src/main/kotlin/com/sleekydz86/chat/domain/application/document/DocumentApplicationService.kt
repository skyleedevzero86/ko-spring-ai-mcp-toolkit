package com.sleekydz86.chat.domain.application.document

import com.sleekydz86.chat.domain.model.document.DocumentRepository
import com.sleekydz86.chat.domain.model.document.DocumentSplitter
import org.springframework.ai.document.Document
import org.springframework.ai.reader.TextReader
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service

@Service
class DocumentApplicationService(
    private val documentRepository: DocumentRepository,
    private val documentSplitter: DocumentSplitter
) {

    fun loadDocument(resource: Resource, fileName: String) {

        val textReader = TextReader(resource)
        textReader.customMetadata["fileName"] = fileName
        val documents = textReader.get()

        val splitDocuments = documentSplitter.split(documents)

        documentRepository.saveAll(splitDocuments)
    }
}