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

package io.fletchly.genius.command

import dagger.Module
import dagger.Provides
import io.fletchly.genius.Genius
import io.fletchly.genius.command.commands.AskCommand
import io.fletchly.genius.command.commands.ClearContextCommand
import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.conversation.service.ConversationManager
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Logger
import javax.inject.Singleton

@Module
class CommandModule {
    @Provides
    @Singleton
    fun provideAskCommand(
        configurationManager: ConfigurationManager,
        plugin: Genius,
        pluginScope: CoroutineScope,
        pluginLogger: Logger,
        conversationManager: ConversationManager,
    ) = AskCommand(configurationManager, plugin, pluginScope, pluginLogger, conversationManager)

    fun provideManageCommand(
        pluginScope: CoroutineScope,
        pluginLogger: Logger,
        contextService: ContextService,
    ) = ClearContextCommand(contextService, pluginScope, pluginLogger)

    @Provides
    @Singleton
    fun provideCommands(
        askCommand: AskCommand,
        clearContextCommand: ClearContextCommand
    ) = listOf(
        askCommand,
        clearContextCommand
    )
}