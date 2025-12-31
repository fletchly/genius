package io.fletchly.genius.service.tool.ollama

import io.fletchly.genius.client.KtorHttpClient
import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.service.tool.Tool
import io.fletchly.genius.service.tool.tool
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class WebSearchTool(
    configuration: GeniusConfiguration,
    ktorHttpClient: KtorHttpClient
): Tool {
    private val httpClient = ktorHttpClient.getClient(OLLAMA_BASE_URL, configuration.ollama.apiKey)

    override val definition = tool {
        name = "web_search"
        description = "search the web"

        parameter(
            name = "query",
            type = "string",
            description = "Search query",
        )

        handle { args ->
            handleTool(args)
        }
    }

    override suspend fun handleTool(args: JsonObject): String {
        val webSearchArgs = Json.decodeFromJsonElement<WebSearchArgs>(args)

        val response = httpClient.post("/api/web_search") {
            contentType(ContentType.Application.Json)
            setBody(webSearchArgs)
        }
        // result is truncated to limit token use
        return response.body<WebSearchResponse>().toString().take(8000)
    }
    private companion object {
        const val OLLAMA_BASE_URL = "https://ollama.com"
    }
}

@Serializable
data class WebSearchArgs(
    val query: String
)

@Serializable
data class WebSearchResponse(
    val results: List<WebSearchResult>
) {
    override fun toString(): String {
        return Json.encodeToString(this)
    }
}

@Serializable
data class WebSearchResult(
    val title: String,
    val url: String,
    val content: String,
)