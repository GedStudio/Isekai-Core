package org.gedstudio.isekai.core.registry

import org.gedstudio.isekai.lib.open.command.CommandSenderInvoker
import org.gedstudio.isekai.lib.open.command.EzCommand
import org.gedstudio.isekai.lib.open.command.EzCommandManager

object CommandRegistry {

    private val ISEKAI_COMMAND = EzCommand
        .literal("isekai")
        .executes(CommandSenderInvoker { sender, argument ->
            sender.sendMessage("isekai.message.text")
            return@CommandSenderInvoker 1
        })

    fun init() {
        reg(ISEKAI_COMMAND)
    }

    private fun reg(cmd: EzCommand) {
        EzCommandManager.getManager().register("isekai", cmd)
    }

}