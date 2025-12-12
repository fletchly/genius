package io.fletchly.genius.ollama.client
import io.fletchly.genius.config.ConfigManager
import io.fletchly.genius.conversation.model.Message
import io.fletchly.genius.ollama.model.OllamaOptions
import io.fletchly.genius.ollama.model.OllamaRequest
import io.fletchly.genius.ollama.model.OllamaResponse
import io.ktor.http.Url
import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class OllamaHttpClientTest {

    private lateinit var configManager: ConfigManager
    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()

        configManager = Mockito.mock(ConfigManager::class.java)
        Mockito.`when`(configManager.ollamaBaseUrl).thenReturn(server.url("/").toString())
        Mockito.`when`(configManager.ollamaApiKey).thenReturn("test-api-key")
    }

    @AfterEach
    fun tearDown() {
        server.close()
    }

    @Test
    fun `constructor should throw HttpClientException when api key is null`() {
        Mockito.`when`(configManager.ollamaApiKey).thenReturn(null)

        val exception = assertThrows<HttpClientException> {
            OllamaHttpClient(configManager)
        }

        assertEquals("No Ollama API key provided!", exception.message)
    }

    @Test
    fun `constructor should initialize with valid config`() {
        val client = OllamaHttpClient(configManager)
        assertEquals(server.url("/").toString(), client.baseUrl)
        assertEquals("test-api-key", client.apiKey)
    }

    @Test
    fun `fetchChatResponse should return OllamaResponse on success`() {
        val responseBody = """
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

        server.enqueue(MockResponse.Builder()
            .body(responseBody)
            .addHeader("Content-Type", "application/json")
            .code(200)
            .build())

        val client = OllamaHttpClient(configManager)

        val request = OllamaRequest(
            model = "gemma3",
            messages = listOf(Message("Hello", Message.USER)),
            options = OllamaOptions(
                temperature = 0.5,
                topK = 40,
                topP = 0.85,
                numPredict = 400
            )
        )

        val response: OllamaResponse
        runBlocking {
            response = client.fetchChatResponse(request)
        }

        assertEquals("gemma3", response.model)
        assertEquals("assistant", response.message.role)
        assertEquals("Hello!", response.message.content)
    }

    @Test
    fun `fetchChatResponse should throw HttpClientException on error`() {
        server.enqueue(MockResponse.Builder()
            .code(418)
            .build()
        )

        val client = OllamaHttpClient(configManager)

        val request = OllamaRequest(
            model = "gemma3",
            messages = listOf(Message("Hello", Message.USER)),
            options = OllamaOptions(
                temperature = 0.5,
                topK = 40,
                topP = 0.85,
                numPredict = 400
            )
        )

        val exception: HttpClientException

        runBlocking {
            exception = assertThrows<HttpClientException> {
                client.fetchChatResponse(request)
            }
        }

        assertEquals("Request to Ollama API failed: Request to Ollama API failed with status: 418 Client Error", exception.message)
    }
}
