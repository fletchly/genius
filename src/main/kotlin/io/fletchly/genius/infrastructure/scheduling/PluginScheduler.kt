/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fletchly.genius.infrastructure.scheduling

import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * Safely exposes the plugin's scheduler
 */
class PluginScheduler(
    private val plugin: JavaPlugin
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun runCoroutine(block: suspend CoroutineScope.() -> Unit) {
        scope.launch(block = block)
    }

    fun cancel() {
        scope.cancel()
    }

    /**
     * Run task from an async context using the plugin's scheduler
     */
    suspend fun <T> runTask(block: () -> T): T {
        if (plugin.server.isPrimaryThread) return block()
        return suspendCancellableCoroutine { continuation ->
            plugin.server.scheduler.runTask(plugin, Runnable {
                continuation.resumeWith(runCatching(block))
            })
        }
    }
}