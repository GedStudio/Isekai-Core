package org.gedstudio.isekai.core

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.gedstudio.isekai.core.listener.CommandFactory
import org.gedstudio.isekai.core.listener.MessageFactory
import org.gedstudio.isekai.core.registry.CommandRegistry
import org.gedstudio.isekai.core.registry.PermissionRegistry
import org.gedstudio.isekai.core.util.Msg
import org.gedstudio.isekai.lib.open.command.EzCommandManager

class IsekaiCore : JavaPlugin() {

    override fun onLoad() {
    }

    override fun onEnable() {
        MessageFactory.init()
        logger.info("Success??")
        Bukkit.getPluginManager()
            .registerEvents(
                (EzCommandManager
                    .getManager() as Listener),
                this
            )
        Bukkit.getPluginManager().registerEvents(CommandFactory, this)
        CommandRegistry.init()
        PermissionRegistry.init()
        Msg.init()
    }

    companion object {

        fun getIsekaiCore(): IsekaiCore {
            return getPlugin(IsekaiCore::class.java)
        }

    }

}