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

/**
 * Displays output to players
 */
interface DisplayService {
    /**
     * Display info message
     */
    suspend fun displayInfo(message: String)

    /**
     * Display message from player
     */
    suspend fun displayPlayerMessage(message: String)

    /**
     * Display message from assistant
     */
    suspend fun displayAssistantMessage(message: String)

    /**
     * Display error message
     */
    suspend fun displayErrorMessage(message: String)
}