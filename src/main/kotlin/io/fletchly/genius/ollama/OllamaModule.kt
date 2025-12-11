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