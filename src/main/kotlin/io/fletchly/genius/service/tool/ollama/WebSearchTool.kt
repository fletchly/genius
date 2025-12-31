/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.service.tool.ollama

import io.fletchly.genius.client.KtorHttpClient
import io.fletchly.genius.manager.config.GeniusConfiguration
import io.fletchly.genius.service.tool.Tool
import io.fletchly.genius.service.tool.tool
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class WebSearchTool(
    configuration: GeniusConfiguration,
    ktorHttpClient: KtorHttpClient
) : Tool {
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

val webSearchModule = module {
    singleOf(::WebSearchTool) { bind<Tool>() }
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