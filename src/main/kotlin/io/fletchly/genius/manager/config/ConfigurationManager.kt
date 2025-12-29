/*
 * This file is part of Genius, licensed under the Apache License 2.0.
 *
 * Copyright (c) 2025 fletchly
 * Copyright (c) 2025 contributors
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

import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.exists

class ConfigurationManager(
    private val logger: Logger,
    plugin: JavaPlugin
) {
    private val configPath = Path.of(plugin.dataFolder.path, "genius.conf")
    private val hoconLoader = ConfigurationLoaders.buildHoconLoader(configPath)

    /**
     * Load configuration from file
     */
    fun loadConfig(): GeniusConfiguration {
        saveDefaultConfig()

        try {
            val root = hoconLoader.load()
            val configuration: GeniusConfiguration? = root.get()

            if (configuration != null) {
                return configuration
            }
        } catch (ex: ConfigurateException) {
            val message = "Couldn't parse configuration"
            logger.severe { "$message: ${ex.message}" }
            throw ConfigurationException(message, ex)
        } catch (ex: SerializationException) {
            val message = "Couldn't convert configuration to object"
            logger.severe { "$message: ${ex.message}" }
            throw ConfigurationException(message, ex)
        }

        throw ConfigurationException("Configuration not present, or not of requested type", null)
    }

    private fun saveDefaultConfig(overwrite: Boolean = false) {
        if (overwrite || !configPath.exists()) {
            val root = hoconLoader.createNode(
                ConfigurationOptions.defaults()
                    .header(GeniusConfiguration.HEADER)
            )
            try {
                root.node().set(GeniusConfiguration::class.java, GeniusConfiguration())
                hoconLoader.save(root)
            } catch (ex: SerializationException) {
                val message = "Couldn't serialize default configuration"
                logger.severe { "$message: ${ex.message}" }
                throw ConfigurationException(message, ex)
            } catch (ex: ConfigurateException) {
                val message = "Error saving default configuration"
                logger.severe { "$message: ${ex.message}" }
                throw ConfigurationException(message, ex)
            }
        }
    }
}