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

package io.fletchly.genius.event

import dagger.Module
import dagger.Provides
import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.event.listeners.PlayerListener
import kotlinx.coroutines.CoroutineScope
import org.bukkit.event.Listener
import java.util.logging.Logger

@Module
class EventModule {
    @Provides
    fun providePlayerListener(
        pluginLogger: Logger,
        pluginScope: CoroutineScope,
        contextService: ContextService
    ) = PlayerListener(pluginLogger, pluginScope, contextService)

    @Provides
    fun provideListeners(playerListener: PlayerListener): List<Listener> = listOf(playerListener)
}