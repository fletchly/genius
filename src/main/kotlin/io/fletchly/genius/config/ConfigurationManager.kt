package io.fletchly.genius.config

import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.serialize.SerializationException
import java.nio.file.Path
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.io.path.exists

class ConfigurationManager @Inject constructor(
    private val logger: Logger,
    plugin: JavaPlugin
) {
    private val configPath = Path.of(plugin.dataFolder.path, "genius.conf")
    private val hoconLoader = ConfigurationLoaders. buildHoconLoader(configPath)

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