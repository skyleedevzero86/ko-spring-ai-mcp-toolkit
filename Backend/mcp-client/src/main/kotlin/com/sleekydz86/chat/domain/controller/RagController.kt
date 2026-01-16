package com.sleekydz86.chat.domain.controller

import com.sleekydz86.chat.domain.application.document.DocumentApplicationService
import com.sleekydz86.chat.global.util.LeeResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/rag")
class RagController(
    private val documentApplicationService: DocumentApplicationService
) {
    @PostMapping("/upload")
    fun upload(@RequestParam("file") file: MultipartFile): LeeResult {
        documentApplicationService.loadDocument(file.resource, file.originalFilename ?: "unknown")
        return LeeResult.ok()
    }
}