package io.fletchly.genius.config

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