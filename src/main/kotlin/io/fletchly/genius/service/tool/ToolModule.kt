package io.fletchly.genius.service.tool

import io.fletchly.genius.service.tool.ollama.WebSearchTool
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val toolModule = module {
    singleOf(::WebSearchTool) { bind<Tool>() }
    singleOf(::ToolService)
}