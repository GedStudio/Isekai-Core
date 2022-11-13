package org.gedstudio.isekai.core

import net.deechael.genshin.lib.impl.world.SlimeManagerImpl
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.gedstudio.isekai.builder.manager.PlayersMapManager
import org.gedstudio.isekai.core.listener.CommandFactory
import org.gedstudio.isekai.core.listener.MessageFactory
import org.gedstudio.isekai.core.registry.CommandRegistry
import org.gedstudio.isekai.core.registry.PermissionRegistry
import org.gedstudio.isekai.core.util.Msg
import org.gedstudio.isekai.lib.open.command.EzCommandManager

class IsekaiCore : JavaPlugin() {

    override fun onLoad() {
        SlimeManagerImpl.getInstance().loading(this)
    }

    override fun onEnable() {
        SlimeManagerImpl.getInstance().enabling()
        PlayersMapManager.init()
        MessageFactory.init()
        Bukkit.getPluginManager()
            .registerEvents(
                (EzCommandManager
                    .getManager() as Listener),
                this
            )
        Bukkit.getPluginManager().registerEvents(CommandFactory, this)
        CommandRegistry.init()
        PermissionRegistry.init()
        Msg.reload()
    }

    override fun onDisable() {
        SlimeManagerImpl.getInstance().disabling()
    }

    companion object {

        fun getIsekaiCore(): IsekaiCore {
            return getPlugin(IsekaiCore::class.java)
        }

    }

}