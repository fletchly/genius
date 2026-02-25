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

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents

import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin

class GeniusCommand (
    val node: LiteralCommandNode<CommandSourceStack>,
    val description: String,
    val aliases: List<String>,
    val permission: String,
    val permissionDescription: String,
    val permissionDefault: PermissionDefault,
    val childPermissions: List<Permission>
)

class GeniusCommandBuilder(private val name: String) {
    var description = ""
    var aliases: List<String> = emptyList()
    var permission = ""
    var permissionDescription = ""
    var permissionDefault: PermissionDefault = PermissionDefault.OP
    var childPermissions: List<Permission> = emptyList()

    private var hasExecutes: Boolean = false
    private var nodeBuilder: LiteralArgumentBuilder<CommandSourceStack> =
        LiteralArgumentBuilder.literal(name)

    fun executes(handler: (CommandSourceStack) -> Int) {
        hasExecutes = true
        nodeBuilder = nodeBuilder.executes { ctx ->
            handler(ctx.source)
        }
    }

    fun node(block: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit) {
        nodeBuilder.apply { block() }
    }

    fun build(): GeniusCommand {
        require(description.isNotBlank()) { "Command $name must have a description" }
        require(permission.isNotBlank()) { "Command $name must have a permission" }
        require(permissionDescription.isNotBlank()) { "Command $name must have a permission message" }
        require(hasExecutes || nodeBuilder.arguments.isNotEmpty()) {
            "Command $name must have either a default handler or provide arguments"
        }

        nodeBuilder = nodeBuilder.requires { it.sender.hasPermission(permission) }

        return GeniusCommand(
            node = nodeBuilder.build(),
            description = description,
            aliases = aliases,
            permission = permission,
            permissionDescription = permissionDescription,
            permissionDefault = permissionDefault,
            childPermissions = childPermissions
        )
    }
}

fun command(
    name: String,
    block: GeniusCommandBuilder.() -> Unit
): GeniusCommand = GeniusCommandBuilder(name).apply(block).build()

fun JavaPlugin.registerCommand(cmd: GeniusCommand) {
    server.pluginManager.addPermission(
        Permission(cmd.permission, cmd.permissionDescription, cmd.permissionDefault)
    )

    cmd.childPermissions.forEach { perm ->
        server.pluginManager.addPermission(perm)
    }

    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) {
        it.registrar().register(
            cmd.node,
            cmd.description,
            cmd.aliases
        )
    }
}