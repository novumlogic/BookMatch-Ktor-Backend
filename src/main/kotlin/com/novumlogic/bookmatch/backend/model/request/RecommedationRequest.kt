package com.novumlogic.bookmatch.backend.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationInput(
    @SerialName("access_token")
    val accessToken: String,
    val messages: List<Message>
)
