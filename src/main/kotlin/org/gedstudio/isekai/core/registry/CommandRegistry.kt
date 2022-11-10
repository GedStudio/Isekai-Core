package org.gedstudio.isekai.core.registry

import org.bukkit.command.ConsoleCommandSender
import org.gedstudio.isekai.core.util.Msg
import org.gedstudio.isekai.lib.open.command.EzCommand
import org.gedstudio.isekai.lib.open.command.EzCommandManager

object CommandRegistry {

    private val ISEKAI_COMMAND = EzCommand
        .literal("isekai")
        .executes { sender, _ ->
            sender.sendMessage("isekai.message.introduction")
            return@executes 1
        }

    private val ISEKAI_RELOAD_COMMAND = EzCommand
        .literal("reload-isekai")
        .requires { sender -> sender.hasPermission("isekai.op") || sender is ConsoleCommandSender }
        .executes { sender, _ ->
            Msg.reload()
            sender.sendMessage("isekai.message.command.isekai-reload.success")
            return@executes 1
        }

    private val HELP_COMMAND = EzCommand
        .literal("help")
        .executes { sender, _ ->
            sender.sendMessage("isekai.message.command.help")
            return@executes 1
        }

    private val PLUGINS_COMMAND = EzCommand
        .literal("plugins")
        .executes { sender, _ ->
            sender.sendMessage("isekai.message.command.plugins")
            return@executes 1
        }

    fun init() {
        reg(ISEKAI_COMMAND)
        reg(ISEKAI_RELOAD_COMMAND)
        reg(HELP_COMMAND)
        reg(PLUGINS_COMMAND)
    }

    private fun reg(cmd: EzCommand) {
        EzCommandManager.getManager().register("isekai", cmd)
    }

}