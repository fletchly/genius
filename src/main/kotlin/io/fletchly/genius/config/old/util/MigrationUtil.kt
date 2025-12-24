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

package io.fletchly.genius.config.old.util

import org.bukkit.configuration.file.FileConfiguration
import javax.inject.Inject

class MigrationUtil @Inject constructor( private val config: FileConfiguration) {
    /**
     * Apply config migrations
     *
     * @return number of migrations performed
     */
    fun migrateConfig(): Int {
        val currentConfigVersion = config.getInt(CONFIG_VERSION_PATH, 0)
        var migrations = 0

        if (currentConfigVersion < 1) {
            config.set("genius.agentPrefix", "\uD83D\uDCA1")
            config.set("genius.playerPrefix", "\uD83D\uDC64")
            config.set("configVersion", 1)
            migrations++
        }

        return migrations
    }

    private companion object {
        const val CONFIG_VERSION_PATH = "configVersion"
    }
}