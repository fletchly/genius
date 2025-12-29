package io.fletchly.genius.config

import org.spongepowered.configurate.NodePath.path
import org.spongepowered.configurate.transformation.ConfigurationTransformation
import org.spongepowered.configurate.transformation.TransformAction

class ConfigurationMigrations {
    companion object {
        fun create() : ConfigurationTransformation.Versioned = ConfigurationTransformation.versionedBuilder()
            .addVersion(VERSION_LATEST, oneToTwo())
            .addVersion(1, zeroToOne())
            .addVersion(0, removeOldVersionNode())
            .build()

        private const val VERSION_LATEST = 2

        private fun removeOldVersionNode() : ConfigurationTransformation = ConfigurationTransformation.builder()
            .addAction(path("configVersion"), TransformAction.remove())
            .build()

        private fun zeroToOne() : ConfigurationTransformation = ConfigurationTransformation.builder()
            .addAction(path("genius")) { _, value ->
                value.node("agentPrefix").set("\uD83D\uDCA1")
                null
            }
            .addAction(path("genius")) {_, value ->
                value.node("playerPrefix").set("\uD83D\uDC64")
                null
            }
            .build()

        private fun oneToTwo() : ConfigurationTransformation = ConfigurationTransformation.builder()
            .addAction(path("genius"), TransformAction.rename("display"))
            .build()
    }
}