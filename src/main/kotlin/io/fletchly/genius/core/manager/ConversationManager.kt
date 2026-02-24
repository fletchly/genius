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

import io.fletchly.genius.core.model.Message
import io.fletchly.genius.core.port.inbound.GenerateAssistantResponse
import io.fletchly.genius.core.port.outbound.AiService
import io.fletchly.genius.core.port.outbound.ContextService
import io.fletchly.genius.core.port.outbound.DisplayService
import io.fletchly.genius.core.port.outbound.ToolService
import java.util.UUID

class ConversationManager(
    val aiService: AiService,
    val contextService: ContextService,
    val displayService: DisplayService,
    val toolService: ToolService
) : GenerateAssistantResponse {
    override suspend fun handlePlayerInput(playerUUID: UUID, content: String) {
        val inputMessage = Message(content, Message.USER)

        displayService.displayPlayerMessage(inputMessage.content)

        contextService.appendContext(playerUUID, inputMessage)
        val responseMessage = generateResponseWithCurrentContext(playerUUID)

        displayService.displayAssistantMessage(responseMessage.content)
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