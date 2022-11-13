package org.gedstudio.isekai.builder.listener

import net.deechael.genshin.lib.open.world.SlimeManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.gedstudio.isekai.builder.manager.HotbarManager

object BuilderListener : Listener {

    @EventHandler
    fun hotbar(event: PlayerItemHeldEvent) {
        val player = event.player
        val loc = player.location
        if (SlimeManager.getManager().getWorld(loc.world.name) == null)
            return
        val previousSlot = event.previousSlot + 1
        val newSlot = event.newSlot + 1
        val max = HotbarManager.getSlots(player)
        val hotbar = HotbarManager.getHotbar(player)
        HotbarManager.setCurrentSlot(player, HotbarManager.getCurrentSlot(player) + (newSlot - previousSlot))
        val current = HotbarManager.getCurrentSlot(player)
        event.isCancelled = true
        if (current >= 5 && current <= max - 4) {
            player.inventory.heldItemSlot = 4
            val base = current - 5
            for (i in 0..8) {
                player.inventory.setItem(i, hotbar[base + i])
            }
        } else if (current < 5) {
            player.inventory.heldItemSlot = current - 1
            for (i in 0..8) {
                player.inventory.setItem(i, hotbar[i])
            }
        } else {
           player.inventory.heldItemSlot = (max - 9) + current - 1
           val base = max - 9
            for (i in 0..8) {
                player.inventory.setItem(i, hotbar[base + i])
            }
        }
    }

    @EventHandler
    fun join(event: PlayerJoinEvent) {

    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        HotbarManager.quit(event.player)
    }

}