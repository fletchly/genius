package io.fletchly.genius.command.util

import io.fletchly.genius.Genius
import javax.inject.Inject

/**
 * Utility class that exposes the plugin scheduler
 */
class PluginSchedulerUtil @Inject constructor(private val plugin: Genius) {
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