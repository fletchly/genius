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

import io.fletchly.genius.core.port.outbound.LoggingService
import io.fletchly.genius.infrastructure.config.ConfigurationManager
import io.fletchly.genius.infrastructure.config.GeniusConfiguration
import io.fletchly.genius.infrastructure.config.SystemPromptManager
import io.fletchly.genius.infrastructure.http.createKtorHttpClient
import io.fletchly.genius.infrastructure.logging.PluginLogger
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.tool.ToolRegistry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val configModule = module {
    singleOf(::ConfigurationManager)
    singleOf(::SystemPromptManager)
    single<GeniusConfiguration> { get<ConfigurationManager>().loadConfig() }
}

private val httpModule = module {
    single { createKtorHttpClient(get()) }
}

private val loggingModule = module {
    singleOf(::PluginLogger) bind LoggingService::class
}

private val schedulingModule = module {
    singleOf(::PluginScheduler)
}

private val toolRegistryModule = module {
    singleOf(::ToolRegistry)
}

val infrastructureModule = module {
    includes(
        configModule,
        httpModule,
        loggingModule,
        schedulingModule,
        toolRegistryModule,
    )
}