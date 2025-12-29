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

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.loader.HeaderMode
import org.spongepowered.configurate.util.MapFactories
import java.nio.file.Path

class ConfigurationLoaders {
    companion object {
        /**
         * Build Configurate HOCON loader
         */
        fun buildHoconLoader(path: Path): AbstractConfigurationLoader<CommentedConfigurationNode> {
            return HOCON
                .path(path)
                .build()
        }

        private val HOCON: HoconConfigurationLoader.Builder = HoconConfigurationLoader.builder()
            .headerMode(HeaderMode.PRESET)
            .prettyPrinting(true)
            .indent(2)
            .emitComments(true)
            .defaultOptions { opts ->
                opts.mapFactory(MapFactories.sortedNatural())
                opts.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }

    }
}