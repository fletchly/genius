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

package io.fletchly.genius.adapter.inbound.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import io.fletchly.genius.core.port.inbound.ManageContext
import io.fletchly.genius.infrastructure.scheduling.PluginScheduler
import io.fletchly.genius.infrastructure.target.ConsoleTarget
import io.fletchly.genius.infrastructure.target.PlayerTarget
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

fun adminCommand(
    manageContext: ManageContext,
    pluginScheduler: PluginScheduler
) = command<CommandSourceStack>("genius") {
    description = "Manage Genius"
    permission = "genius.manage"
    permissionDescription = "Allow a player to manage genius"
    permissionDefault = PermissionDefault.TRUE

    val clearSelfPermission = Permission(
        "genius.manage.clear",
        "Allow a player to clear their own context",
        PermissionDefault.TRUE
    )

    val clearOtherPermission = Permission(
        "genius.manage.clear.other",
        "Allow a player to clear other's context",
        PermissionDefault.OP
    )

    childPermissions = listOf(
        clearSelfPermission,
        clearOtherPermission
    )

    node {
        then(
            Commands.literal("clear")
                .requires { source ->
                    source.sender.hasPermission(clearSelfPermission.name)
                }
                .executes { ctx ->
                    val target = when (val sender = ctx.source.sender) {
                        is Player -> PlayerTarget(sender)
                        else -> ConsoleTarget
                    }

                    pluginScheduler.runCoroutine {
                        manageContext.clearContext(target, target.uniqueId)
                    }

                    Command.SINGLE_SUCCESS
                }
                .then(
                    Commands.argument("targets", ArgumentTypes.players())
                        .requires { source ->
                            source.sender.hasPermission(clearOtherPermission.name)
                                    && source.sender.hasPermission("minecraft.command.selector")
                        }
                        .executes { ctx ->
                            val targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver::class.java)
                            val targets = targetResolver.resolve(ctx.source)
                            val feedbackTarget = when (val sender = ctx.source.sender) {
                                is Player -> PlayerTarget(sender)
                                else -> ConsoleTarget
                            }

                            pluginScheduler.runCoroutine {
                                manageContext.clearContext(
                                    feedbackTarget,
                                    *targets
                                        .map { it.uniqueId }
                                        .toTypedArray()
                                )
                            }

                            Command.SINGLE_SUCCESS
                        }
                )
        )
    }
}