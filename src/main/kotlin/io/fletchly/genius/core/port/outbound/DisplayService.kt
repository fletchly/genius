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

import io.fletchly.genius.core.model.Target

/**
 * Displays output to players
 */
interface DisplayService {
    /**
     * Display message from player
     *
     * @param target Target to display message for
     * @param message Message to display
     */
    suspend fun displayPlayerMessage(target: Target, message: String)

    /**
     * Display message from assistant
     *
     * @param target Target to display message for
     * @param message Message to display
     */
    suspend fun displayAssistantMessage(target: Target, message: String)

    /**
     * Display info message
     *
     * @param target Target to display message for
     * @param message Message to display
     */
    suspend fun displayInfoMessage(target: Target, message: String)

    /**
     * Display error message
     *
     * @param target Target to display message for
     * @param message Message to display
     */
    suspend fun displayErrorMessage(target: Target, message: String)
}