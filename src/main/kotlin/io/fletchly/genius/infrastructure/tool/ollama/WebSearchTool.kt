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

package io.fletchly.genius.infrastructure.tool.ollama

import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.http.HttpClientException
import io.fletchly.genius.infrastructure.tool.ParamType
import io.fletchly.genius.infrastructure.tool.Tool
import io.fletchly.genius.infrastructure.tool.ToolResult
import io.fletchly.genius.infrastructure.tool.tool
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

const val OLLAMA_BASE_URL = "https://ollama.com"

fun webSearchTool(
    httpClient: HttpClient,
    configuration: GeniusConfiguration
) = tool("web_search") {
    description = "Search the web for answers"

    parameter("query") {
        type = ParamType.STRING
        description = "Search query"
        required = true
    }

    handler { args ->
        val params = try {
            Json.decodeFromJsonElement<WebSearchParams>(args)
        } catch (_: Exception) {
            return@handler ToolResult.Failure("Missing/invalid search query")
        }
        try {
            val response = httpClient.post("$OLLAMA_BASE_URL/api/web_search") {
                if (configuration.ollama.apiKey != null) bearerAuth(configuration.ollama.apiKey)
                contentType(ContentType.Application.Json)
                setBody(params)
            }
            ToolResult.Success(response.body<JsonElement>())
        } catch (ex: HttpClientException) {
            ToolResult.Failure(ex.message)
        } catch (_: Exception) {
            ToolResult.Failure("An unknown error occurred")
        }
    }
}

@Serializable
private data class WebSearchParams(
    val query: String
)

val webSearchToolModule = module {
    single(named("web_search")) { webSearchTool(get(), get()) } bind Tool::class
}