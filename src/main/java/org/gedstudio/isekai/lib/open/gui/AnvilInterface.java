package org.gedstudio.isekai.lib.open.gui;

import org.bukkit.inventory.Inventory;

public interface AnvilInterface {

    void open(String title);

    Inventory castToBukkit();

    int getWindowId();

}
