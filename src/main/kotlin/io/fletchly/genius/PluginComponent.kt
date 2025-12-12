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

package io.fletchly.genius

import dagger.Component
import io.fletchly.genius.command.GeniusCommand
import io.fletchly.genius.command.CommandModule
import io.fletchly.genius.config.ConfigModule
import io.fletchly.genius.context.ContextModule
import io.fletchly.genius.conversation.ConversationModule
import io.fletchly.genius.listeners.ListenerModule
import io.fletchly.genius.listeners.PlayerListener
import io.fletchly.genius.ollama.OllamaModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        PluginModule::class,
        ConfigModule::class,
        ContextModule::class,
        CommandModule::class,
        ConversationModule::class,
        OllamaModule::class,
        ListenerModule::class
    ]
)
interface PluginComponent {
    fun commands(): List<GeniusCommand>

    fun playerListener(): PlayerListener
}