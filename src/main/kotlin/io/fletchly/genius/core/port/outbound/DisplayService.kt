/*
 * This file is part of Genius, licensed under the Apache License 2.0
 *
 * Copyright (c) 2026 fletchly
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

package io.fletchly.genius.core.port.outbound

import java.util.*

/**
 * Displays output to players
 */
interface DisplayService {
    /**
     * Display message from player
     *
     * @param playerUUID UUID of player to display message for
     * @param message Message to display
     */
    suspend fun displayPlayerMessage(playerUUID: UUID, message: String)

    /**
     * Display message from assistant
     *
     * @param playerUUID UUID of player to display message for
     * @param message Message to display
     */
    suspend fun displayAssistantMessage(playerUUID: UUID, message: String)

    /**
     * Display error message
     *
     * @param playerUUID UUID of player to display message for
     * @param message Message to display
     */
    suspend fun displayErrorMessage(playerUUID: UUID, message: String)
}