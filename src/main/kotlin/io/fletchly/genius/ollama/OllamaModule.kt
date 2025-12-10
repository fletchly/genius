package io.fletchly.genius.ollama

import dagger.Module
import dagger.Provides
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.ollama.client.HttpClient
import io.fletchly.genius.ollama.client.OllamaHttpClient
import javax.inject.Singleton

@Module
class OllamaModule {
    @Provides
    @Singleton
    fun provideHttpClient(configManager: ConfigManager): HttpClient = OllamaHttpClient(configManager)
}