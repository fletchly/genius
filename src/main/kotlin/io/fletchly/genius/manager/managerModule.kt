package io.fletchly.genius.manager

import io.fletchly.genius.manager.config.configModule
import io.fletchly.genius.manager.conversation.conversationModule
import org.koin.dsl.module

val managerModule = module {
    includes(configModule, conversationModule)
}