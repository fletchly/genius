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

package io.fletchly.genius.infrastructure.di

import io.fletchly.genius.adapter.inbound.command.GeniusCommand
import io.fletchly.genius.adapter.inbound.command.adminCommand
import io.fletchly.genius.adapter.inbound.command.askCommand
import io.fletchly.genius.adapter.inbound.event.PlayerEvents
import io.fletchly.genius.adapter.outbound.ai.ollama.OllamaAiService
import io.fletchly.genius.adapter.outbound.context.ConcurrentHashMapContextService
import io.fletchly.genius.adapter.outbound.display.MinecraftChatDisplayService
import io.fletchly.genius.adapter.outbound.tool.BasicToolService
import io.fletchly.genius.core.port.outbound.AiService
import io.fletchly.genius.core.port.outbound.ContextService
import io.fletchly.genius.core.port.outbound.DisplayService
import io.fletchly.genius.core.port.outbound.ToolService
import org.bukkit.event.Listener
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val commandModule = module {
    single { askCommand(get(), get()) } bind GeniusCommand::class
    single { adminCommand(get(), get()) } bind GeniusCommand::class
}

private val eventModule = module {
    singleOf(::PlayerEvents) bind Listener::class
}

private val aiModule = module {
    singleOf(::OllamaAiService) bind AiService::class
}

private val contextModule = module {
    singleOf(::ConcurrentHashMapContextService) bind ContextService::class
}

private val displayModule = module {
    singleOf(::MinecraftChatDisplayService) bind DisplayService::class
}

private val toolModule = module {
    singleOf(::BasicToolService) bind ToolService::class
}

val adapterModule = module {
    includes(
        commandModule,
        eventModule,
        aiModule,
        contextModule,
        displayModule,
        toolModule
    )
}