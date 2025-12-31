/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2025 fletchly
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

package io.fletchly.genius.util

import org.bukkit.plugin.java.JavaPlugin

/**
 * Utility class that exposes the plugin scheduler
 */
class PluginSchedulerUtil(private val plugin: JavaPlugin) {
    /**
     * Use the plugin scheduler to run a task
     *
     * Used to safely touch the Bukkit API from an asynchronous context
     *
     * @param task the task to run
     */
    fun runTask(task: Runnable) {
        plugin.server.scheduler.runTask(plugin, task)
    }
}