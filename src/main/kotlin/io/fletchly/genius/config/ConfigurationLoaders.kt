package io.fletchly.genius.config

import com.hpfxd.configurate.eoyaml.EOYamlConfigurationLoader
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.loader.HeaderMode
import org.spongepowered.configurate.util.MapFactories
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.Path

class ConfigurationLoaders {
    companion object {
        /**
         * Get the loader used for handling YAML files with configurate
         *
         * @param path the path of the file for loading
         * @return A configurate loader
         */
        fun getEoYamlLoader(path: Path): AbstractConfigurationLoader<CommentedConfigurationNode> {
            return EO_YAML
                .path(path)
                .build()
        }

        fun getDefaultYamlLoader(path: Path): AbstractConfigurationLoader<CommentedConfigurationNode> {
            return DEFAULT_YAML
                .path(path)
                .build()
        }

        /**
         * Default Configurate YAML loader
         */
        val DEFAULT_YAML: YamlConfigurationLoader.Builder = YamlConfigurationLoader.builder()
            .indent(2)
            .nodeStyle(NodeStyle.BLOCK)
            .headerMode(HeaderMode.PRESET)
            .defaultOptions { opts ->
                opts.mapFactory(MapFactories.sortedNatural())
                opts.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }

        /**
         * Custom Configurate YAML loader implementation that uses eo-yaml under the hood
         *
         * Provides support for working with YAML comments.
         */
        val EO_YAML: EOYamlConfigurationLoader.Builder = EOYamlConfigurationLoader.builder()
            .headerMode(HeaderMode.PRESET)
            .defaultOptions { opts ->
                opts.mapFactory(MapFactories.sortedNatural())
                opts.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }
    }
}