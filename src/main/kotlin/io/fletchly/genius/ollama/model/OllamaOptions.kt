package io.fletchly.genius.ollama.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OllamaOptions(
    val temperature: Double,
    @SerialName("top_k")
    val topK: Int,
    @SerialName("top_p")
    val topP: Double,
    @SerialName("num_predict")
    val numPredict: Int
)
