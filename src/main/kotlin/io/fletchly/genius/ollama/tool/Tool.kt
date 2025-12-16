package io.fletchly.genius.ollama.tool

/**
 * Base tool type
 *
 * @property name the tool's name, in snake_case
 * @property description description of tool's function
 * @param Params type of tool parameters
 * @param Result tool return type
 */
sealed interface Tool<Params, Result> {
    val name: String
    val description: String

    /**
     * Execute tool
     *
     * @param params parameter object to be passed to tool
     * @return
     */
    suspend fun execute(params: Params): Result
}