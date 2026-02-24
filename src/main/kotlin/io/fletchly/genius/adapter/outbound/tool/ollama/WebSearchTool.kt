/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.genius.adapter.outbound.tool.ollama

import io.fletchly.genius.adapter.outbound.tool.ToolException
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.http.HttpClientException
import io.fletchly.genius.infrastructure.tool.Tool
import io.fletchly.genius.infrastructure.tool.tool
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class WebSearchTool(
    configuration: GeniusConfiguration,
    private val httpClient: HttpClient
): Tool {
    private val apiKey = configuration.ollama.apiKey
    private val responseLimit = configuration.tool.webSearch.truncateResults

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
        val webSearchArgs = try {
            Json.decodeFromJsonElement<WebSearchArgs>(args)
        } catch (_: Exception) {
            throw ToolException("Error parsing tool arguments")
        }

        try {
            val response = httpClient.post("$OLLAMA_BASE_URL/api/web_search") {
                if (apiKey != null) bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(webSearchArgs)
            }

            val responseText = response.body<WebSearchResponse>().toString().take(responseLimit)

            return "$responseText${
                if (responseText.length > responseLimit) "... (truncated for performance)"
                else ""
            }"
        } catch (_: HttpClientException.ConfigurationError) {
            throw ToolException("Configuration has errors")
        } catch (_: HttpClientException.TimeoutError) {
            throw ToolException("Request timed out")
        } catch (_: HttpClientException.NetworkError) {
            throw ToolException("Network ran into an error")
        } catch (_: HttpClientException.ServerError) {
            throw ToolException("External server encountered an error")
        } catch (_: HttpClientException.ClientError) {
            throw ToolException("Error with client request")
        } catch (_: Exception) {
            throw ToolException("An unknown error occurred")
        }
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