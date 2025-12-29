/*
 * This file is part of Genius, licensed under the Apache License 2.0.
 *
 * Copyright (c) 2025 fletchly
 * Copyright (c) 2025 contributors
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

package io.fletchly.genius.manager.conversation.model

import io.fletchly.genius.manager.conversation.model.Message.Role.ASSISTANT
import io.fletchly.genius.manager.conversation.model.Message.Role.SYSTEM
import io.fletchly.genius.manager.conversation.model.Message.Role.TOOL
import io.fletchly.genius.manager.conversation.model.Message.Role.USER
import kotlinx.serialization.Serializable

/**
 * Universal message model
 *
 * @property content message content
 * @property role role of message sender
 */
@Serializable
data class Message(
    val content: String,
    val role: String
) {
    /**
     * Sender roles
     *
     * @property SYSTEM system instructions (system prompts)
     * @property USER messages from the user
     * @property ASSISTANT messages from the assistant
     * @property TOOL messages from tools
     */
    companion object Role {
        const val SYSTEM = "system"
        const val USER = "user"
        const val ASSISTANT = "assistant"
        const val TOOL = "tool"
    }
}