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

package io.fletchly.genius.ollama

import dagger.Module
import dagger.Provides
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.config.PromptManager
import io.fletchly.genius.ollama.client.HttpClient
import io.fletchly.genius.ollama.client.OllamaHttpClient
import io.fletchly.genius.ollama.service.ChatService
import io.fletchly.genius.ollama.service.OllamaChatService
import javax.inject.Singleton

@Module
class OllamaModule {
    @Provides
    @Singleton
    fun provideHttpClient(configManager: ConfigManager): HttpClient = OllamaHttpClient(configManager)

    @Provides
    @Singleton
    fun provideChatService(
        configManager: ConfigManager,
        promptManager: PromptManager,
        httpClient: HttpClient
    ): ChatService = OllamaChatService(configManager, promptManager, httpClient)
}