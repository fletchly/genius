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
import io.fletchly.genius.command.commands.manage.InfoCommand
import io.fletchly.genius.command.commands.manage.ClearContextCommand
import io.fletchly.genius.command.commands.manage.ManageCommand
import io.fletchly.genius.command.util.ChatMessageUtil
import io.fletchly.genius.command.util.PluginSchedulerUtil
import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.context.service.ContextService
import io.fletchly.genius.conversation.service.ConversationManager
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Logger
import javax.inject.Singleton

@Module
class CommandModule {
    @Provides
    fun provideChatMessageUtil(configurationManager: ConfigurationManager) = ChatMessageUtil(configurationManager)

    @Provides
    @Singleton
    fun providePluginSchedulerUtil(plugin: Genius) = PluginSchedulerUtil(plugin)

    @Provides
    @Singleton
    fun provideAskCommand(
        configurationManager: ConfigurationManager,
        pluginSchedulerUtil: PluginSchedulerUtil,
        pluginScope: CoroutineScope,
        pluginLogger: Logger,
        conversationManager: ConversationManager,
        chatMessageUtil: ChatMessageUtil
    ) = AskCommand(configurationManager, pluginSchedulerUtil, pluginScope, pluginLogger, conversationManager, chatMessageUtil)

    @Provides
    @Singleton
    fun provideClearContextCommand(
        pluginScope: CoroutineScope,
        pluginLogger: Logger,
        contextService: ContextService,
        chatMessageUtil: ChatMessageUtil
    ) = ClearContextCommand(contextService, pluginScope, pluginLogger, chatMessageUtil)

    @Provides
    @Singleton
    fun provideInfoCommand(
        configurationManager: ConfigurationManager,
        contextService: ContextService,
        pluginScope: CoroutineScope
    ) = InfoCommand(configurationManager, contextService, pluginScope)

    @Provides
    @Singleton
    fun provideManageCommand(
        infoCommand: InfoCommand,
        clearContextCommand: ClearContextCommand
    ) = ManageCommand(clearContextCommand, infoCommand)

    @Provides
    @Singleton
    fun provideCommands(
        askCommand: AskCommand,
        manageCommand: ManageCommand
    ) = listOf(
        askCommand,
        manageCommand
    )
}