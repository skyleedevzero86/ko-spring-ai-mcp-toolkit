package com.sleekydz86.chat.global.bean

import com.fasterxml.jackson.annotation.JsonProperty
import com.sleekydz86.chat.global.enum.ChatMode

data class ChatEntity(
    @JsonProperty("currentUserName")
    val currentUserName: String? = null,

    @JsonProperty("message")
    val message: String? = null,

    @JsonProperty("botMsgId")
    val botMsgId: String? = null,

    @JsonProperty("mode")
    val mode: ChatMode? = null
)