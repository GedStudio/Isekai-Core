package org.gedstudio.isekai.lib.open.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface AnvilOutputImage extends AnvilOutputSlot {

    ItemStack draw(Player viewer, Inventory inventory, String outputName);

}
