package io.fletchly.genius.ollama.client

import io.fletchly.genius.config.manager.ConfigurationManager
import io.fletchly.genius.conversation.model.Message
import io.fletchly.genius.ollama.model.OllamaOptions
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import java.util.logging.Logger
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class OllamaHttpClientTests {
    private lateinit var configurationManager: ConfigurationManager
    private lateinit var pluginLogger: Logger
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()

        pluginLogger = Mockito.mock(Logger::class.java)

        configurationManager = Mockito.mock(ConfigurationManager::class.java)
        Mockito.`when`(configurationManager.ollamaBaseUrl).thenReturn(server.url("/").toString())
        Mockito.`when`(configurationManager.ollamaApiKey).thenReturn("test-api-key")
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }

    @Test
    fun `chat should throw ConfigurationError on request when apiKey is null`() {
        Mockito.`when`(configurationManager.ollamaApiKey).thenReturn(null)

        val exception = assertThrows<GeniusHttpClientException.ConfigurationError> {
            runBlocking {
                OllamaHttpClient(pluginLogger, configurationManager).chat(GOOD_REQUEST)
            }
        }

        assertEquals("No Ollama API key provided!", exception.message)
    }

    @Test
    fun `chat should throw ClientError on 4xx server response`() {
        server.enqueue(
            MockResponse.Builder()
            .code(418)
            .build()
        )

        val exception = assertThrows<GeniusHttpClientException.ClientError> {
            runBlocking {
                OllamaHttpClient(pluginLogger, configurationManager).chat(GOOD_REQUEST)
            }
        }

        assertEquals("Client error: 418 Client Error", exception.message)
    }

    // FIXME: Refactor this test to not hold up overall test suite
//    @Test
//    fun `chat should throw TimeoutError on 5xx server timeout`() {
//        server.enqueue(
//            MockResponse.Builder()
//                .code(500)
//                .build()
//        )
//
//        val exception = assertThrows<GeniusHttpClientException.TimeoutError> {
//            runBlocking {
//                OllamaHttpClient(pluginLogger, configurationManager).chat(GOOD_REQUEST)
//            }
//        }
//
//        assertEquals("Request timed out", exception.message)
//    }

    @Test
    fun `chat should return valid OllamaResponse on successful response`() {
        server.enqueue(MockResponse.Builder()
            .body(GOOD_RESPONSE)
            .addHeader("Content-Type", "application/json")
            .code(200)
            .build())

        val response = runBlocking {
            OllamaHttpClient(pluginLogger, configurationManager).chat(GOOD_REQUEST)
        }

        assertInstanceOf<OllamaResponse>(response)
        assertEquals("gemma3", response.model)
        assertEquals("assistant", response.message.role)
        assertEquals("Hello!", response.message.content)
    }

    private companion object {
        val GOOD_REQUEST = OllamaRequest(
            model = "gemma3",
            messages = listOf(Message("Hello", Message.USER)),
            options = OllamaOptions(
                temperature = 0.5,
                topK = 40,
                topP = 0.85,
                numPredict = 400
            )
        )

        val GOOD_RESPONSE = """
            {
              "model": "gemma3",
              "created_at": "2025-10-17T23:14:07.414671Z",
              "message": {
                "role": "assistant",
                "content": "Hello!"
              },
              "done": true,
              "done_reason": "stop",
              "total_duration": 174560334,
              "load_duration": 101397084,
              "prompt_eval_count": 11,
              "prompt_eval_duration": 13074791,
              "eval_count": 18,
              "eval_duration": 52479709
            }
        """.trimIndent()
    }
}