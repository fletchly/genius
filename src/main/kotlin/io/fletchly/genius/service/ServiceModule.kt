package io.fletchly.genius.service

import io.fletchly.genius.service.context.contextModule
import io.fletchly.genius.service.chat.ollamaModule
import io.fletchly.genius.service.tool.toolModule
import org.koin.dsl.module

val serviceModule = module {
    includes(contextModule, ollamaModule, toolModule)
}