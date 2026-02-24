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

package io.fletchly.genius.core.manager

import io.fletchly.genius.core.exception.AiProviderException
import io.fletchly.genius.core.model.Message
import io.fletchly.genius.core.model.Target
import io.fletchly.genius.core.port.inbound.GenerateAssistantResponse
import io.fletchly.genius.core.port.outbound.*
import java.util.*

/**
 * Manages conversations between assistant and player
 */
class ConversationManager(
    private val aiService: AiService,
    private val contextService: ContextService,
    private val displayService: DisplayService,
    private val loggingService: LoggingService,
    private val toolService: ToolService
) : GenerateAssistantResponse {
    override suspend fun handleInput(target: Target, content: String) {
        val inputMessage = Message(content, Message.USER)

        displayService.displayPlayerMessage(target, inputMessage.content)
        loggingService.logPlayerMessage(target, inputMessage.content)

        contextService.appendContext(target.uniqueId, inputMessage)

        try {
            val responseMessage = generateResponseWithCurrentContext(target.uniqueId)
            displayService.displayAssistantMessage(target, responseMessage.content)
            loggingService.logAssistantMessage(target, responseMessage.content)
        } catch (ex: AiProviderException) {
            displayService.displayErrorMessage(target, "Error generating response: ${ex.message}")
        }

    }

    private suspend fun generateResponseWithCurrentContext(playerUUID: UUID): Message {
        val context = contextService.getContext(playerUUID)
        val response = aiService.generateResponse(context)

        if (response.toolCalls.isNullOrEmpty()) return response

        for (toolCall in response.toolCalls) {
            val toolMessage = toolService.executeToolCall(toolCall)
            contextService.appendContext(playerUUID, toolMessage)
        }

        return generateResponseWithCurrentContext(playerUUID)
    }
}