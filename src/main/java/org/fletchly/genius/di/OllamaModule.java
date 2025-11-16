package org.fletchly.genius.di;

import dagger.Module;
import dagger.Provides;
import org.fletchly.genius.ollama.OllamaService;
import org.fletchly.genius.ollama.OllamaServiceImpl;
import org.fletchly.genius.ollama.client.OllamaClient;
import org.fletchly.genius.ollama.client.OllamaClientImpl;
import org.fletchly.genius.util.ConfigurationManager;

import javax.inject.Singleton;
import java.net.http.HttpClient;

@Module
public class OllamaModule {
    @Provides
    @Singleton
    HttpClient provideHttpClient() {
        return HttpClient.newHttpClient();
    }

    @Provides
    @Singleton
    OllamaClient provideOllamaClient(ConfigurationManager configurationManager, HttpClient httpClient) {
        return new OllamaClientImpl(configurationManager, httpClient);
    }

    @Provides
    @Singleton
    OllamaService provideOllamaService(ConfigurationManager configurationManager, OllamaClient ollamaClient) {
        return new OllamaServiceImpl(configurationManager, ollamaClient);
    }
}
