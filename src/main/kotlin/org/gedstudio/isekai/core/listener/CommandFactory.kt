package org.gedstudio.isekai.core.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandSendEvent

object CommandFactory : Listener {

    private val RAW_ALLOWED_COMMANDS =
        listOf(
            "help",
            "isekai",
            "plugin"
        )

    private val ALLOWED_COMMANDS: List<String>

    init {
        val allowedCommand = ArrayList<String>()
        RAW_ALLOWED_COMMANDS.forEach {
            allowedCommand.add(it)
            allowedCommand.add("isekai:$it")
        }
        ALLOWED_COMMANDS = allowedCommand.toList()
    }

    @EventHandler
    fun onSendingCommand(event: PlayerCommandSendEvent) {
        if (event.player.hasPermission("isekai.op"))
            return
        event.commands
            .filter { it !in ALLOWED_COMMANDS }
            .forEach(event.commands::remove)
    }

}