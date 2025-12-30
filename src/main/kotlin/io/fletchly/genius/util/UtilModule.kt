package io.fletchly.genius.util

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val utilModule = module {
    singleOf(::ChatMessageUtil)
    singleOf(::PluginSchedulerUtil)
}