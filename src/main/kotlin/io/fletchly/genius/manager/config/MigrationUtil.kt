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

package io.fletchly.genius.manager.config

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.NodePath.path
import org.spongepowered.configurate.transformation.ConfigurationTransformation

object MigrationUtil {
    private const val VERSION_LATEST = 1

    fun create() = ConfigurationTransformation.versionedBuilder()
        .addVersion(VERSION_LATEST, zeroToOne())
        .build()

    private fun zeroToOne() = ConfigurationTransformation.builder()
        .addAction(path()) { _, value ->
            val tool = value.node("tool", "web-search")
            tool.node("enabled").set(true)
            tool.node("truncate-results").set(8000)

            val logging = value.node("logging")
            logging.node("log-http-requests").set(false)
            logging.node("log-player-messages").set(false)
            logging.node("log-tool-calls").set(false)

            null
        }
        .build()
}