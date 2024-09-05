package com.novumlogic.bookmatch.backend.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAiRequest(
    val model: String,
    @SerialName("response_format")
    val responseFormat: JsonElement,
    val messages: MutableList<Message>
)


@Serializable
data class Message(
    val role: String,
    val content: String,
)