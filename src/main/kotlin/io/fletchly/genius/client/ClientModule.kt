package io.fletchly.genius.client

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val clientModule = module {
    singleOf(::KtorHttpClient)
}