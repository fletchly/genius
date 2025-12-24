package io.fletchly.genius.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class GeniusConfiguration(
    @Setting("display")
    @Comment("Properties for the agent's appearance and behavior")
    val display: DisplayConfiguration = DisplayConfiguration(),

    @Setting("ollama")
    @Comment("Ollama client configuration")
    val ollama: OllamaConfiguration = OllamaConfiguration(),

    @Setting("context")
    @Comment("Context store configuration")
    val context: ContextConfiguration = ContextConfiguration(),

    @Setting("version")
    @Comment("Don't change this. Doing so could overwrite existing config")
    val version: Int = 2
) {

    @ConfigSerializable
    data class DisplayConfiguration(
        @Setting("agent-name")
        @Comment("The name used when displaying messages from the agent in chat")
        val agentName: String = "Genius",

        @Setting("agent-prefix")
        @Comment("The prefix used when displaying messages from the agent in chat")
        val agentPrefix: String = "\uD83D\uDCA1",

        @Setting("player-prefix")
        @Comment("The prefix used when displaying messages from players in chat")
        val playerPrefix: String = "\uD83D\uDC64"
    )

    @ConfigSerializable
    data class OllamaConfiguration(
        @Setting("base-url")
        @Comment("The base URL for the Ollama API. For more info, see https://fletchly.github.io/genius-wiki/docs/setup/hosting")
        val baseUrl: String = "https://ollama.com",

        @Setting("api-key")
        @Comment("Your key for the Ollama cloud API. This only needs to be set if you are using an Ollama cloud model.")
        val apiKey: String? = "",

        @Setting("model")
        @Comment("The name of the model to use for response generation.")
        val model: String = "deepseek-v3.1:671b",

        @Setting("temperature")
        @Comment("Controls how random or deterministic the output is.")
        val temperature: Double = 0.5,

        @Setting("top-k")
        @Comment("Restricts sampling to the K most probable next tokens, making output more focused (low values) or more creative (high values).")
        val topK: Int = 40,

        @Setting("top-p")
        @Comment("Limits sampling to the smallest group of likely next tokens that together reach probability P, for more focused (low P) or creative (high P) output.")
        val topP: Double = 0.85,

        @Setting("num-predict")
        @Comment("Sets the maximum number of tokens the model can generate in its response (higher values allow longer outputs; lower values keep them shorter)")
        val numPredict: Int = 400
    )

    @ConfigSerializable
    data class ContextConfiguration(
        @Setting("max-player-messages")
        @Comment("Maximum number of messages per player to store at one time")
        val maxPlayerMessages: Int = 20
    )

    companion object {
        val HEADER = """
            |*****************************************
            |*         Genius Configuration          *
            |*****************************************
            |For reference, see https://fletchly.github.io/genius-wiki/docs/setup/configuration/config-yml
        """.trimMargin()
    }
}
