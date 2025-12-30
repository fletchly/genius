package io.fletchly.genius.service.tool.ollama

import io.fletchly.genius.client.KtorHttpClient
import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.service.tool.Tool
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

class WebSearchTool(
    configuration: GeniusConfiguration,
    ktorHttpClient: KtorHttpClient
): Tool {
    private val httpClient = ktorHttpClient.getClient(OLLAMA_BASE_URL, configuration.ollama.apiKey)

    override val definition = _root_ide_package_.io.fletchly.genius.service.tool.tool {
        name = "web_search"
        description = "search the web"

        parameter(
            name = "query",
            type = "string",
            description = "Search query",
        )

        handle { args ->
            val query = args["query"] as String

            val response = httpClient.post("/api/web_search") {
                contentType(ContentType.Application.Json)
                setBody(WebSearchRequest(query))
            }
            response.body<WebSearchResponse>()
        }
    }

    private companion object {
        const val OLLAMA_BASE_URL = "https://ollama.com"
    }
}

@Serializable
data class WebSearchRequest(
    val query: String
)

@Serializable
data class WebSearchResponse(
    val results: List<WebSearchResult>
)

@Serializable
data class WebSearchResult(
    val title: String,
    val url: String,
    val content: String,
)