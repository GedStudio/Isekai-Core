package org.gedstudio.isekai.lib.open.gui;

import net.deechael.useless.function.parameters.DuParameter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.gedstudio.isekai.core.IsekaiCore;
import org.gedstudio.isekai.core.util.Msg;
import org.gedstudio.isekai.lib.open.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NormalGUI implements Listener {

    private final Plugin plugin;
    private final Map<Player, Inventory> cache = new HashMap<>();
    private final Map<Player, NormalGUIHolder> holders = new HashMap<>();
    private final Map<Integer, Slot> inputs = new HashMap<>();
    private final Type type;
    private final String title;
    private boolean dropped = false;

    private DuParameter<Player, Inventory> onClose = (player, inventory) -> {
    };

    public NormalGUI(Plugin plugin, Type type) {
        this(plugin, type, "GUI");
    }

    public NormalGUI(Plugin plugin, Type type, String title) {
        this.plugin = plugin;
        this.type = type;
        this.title = title;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void dropOnClose() {
        this.onClose = (player, inventory) -> {
            this.drop();
        };
    }

    public void setOnClose(DuParameter<Player, Inventory> onClose) {
        this.onClose = onClose;
    }

    public boolean hasItem(int slot) {
        if (isDropped()) return false;
        return inputs.containsKey(slot);
    }

    public Slot getItem(int slot) {
        if (isDropped()) throw new RuntimeException("This GUI has been dropped!");
        if (!hasItem(slot)) throw new RuntimeException("The slot is empty");
        return inputs.get(slot);
    }

    public void fill(Slot input) {
        for (int i = 0; i < this.type.getSlots(); i++) {
            setItem(i, input);
        }
    }

    public void setItem(int slot, Slot input) {
        if (isDropped()) return;
        inputs.put(slot, input);
        for (Map.Entry<Player, Inventory> entry : cache.entrySet()) {
            if (input instanceof Image image) {
                entry.getValue().setItem(slot, image.draw(entry.getKey(), entry.getValue()));
            }
        }
    }

    public Slot remove(int slot) {
        if (isDropped()) throw new RuntimeException("The GUI has been dropped!");
        return inputs.remove(slot);
    }

    public Inventory getBukkit(Player player) {
        if (isDropped() || !cache.containsKey(player))
            throw new RuntimeException("The gui which player is watching is not this gui!");
        return cache.get(player);
    }


    public void open(Player player) {
        if (isDropped()) return;
        Inventory inventory;
        NormalGUIHolder holder = new NormalGUIHolder(StringUtils.random64());
        if (title != null) {
            inventory = Bukkit.createInventory(holder, this.type.getSlots(), Msg.INSTANCE.mini().deserialize(title));
        } else {
            inventory = Bukkit.createInventory(holder, this.type.getSlots());
        }
        holder.setInventory(inventory);
        for (int i : inputs.keySet()) {
            Slot input = inputs.get(i);
            if (input instanceof Image) {
                inventory.setItem(i, ((Image) input).draw(player, inventory));
            }
        }
        player.openInventory(inventory);
        cache.put(player, inventory);
        holders.put(player, holder);
    }

    public boolean isDropped() {
        return this.dropped;
    }

    public void drop() {
        if (isDropped()) return;
        HandlerList.unregisterAll(this);
        try {
            cache.keySet().forEach(Player::closeInventory);
        } catch (Exception ignored) {
        }
        dropped = true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isDropped()) return;
        if (event.getWhoClicked() instanceof Player player) {
            if (cache.containsKey(player)) {
                if (event.getInventory().getHolder() instanceof NormalGUIHolder holder) {
                    if (holder.getId().equals(holders.get(player).getId())) {
                        if (Objects.equals(event.getClickedInventory(), event.getView().getTopInventory())) {
                            event.setCancelled(true);
                        } else {
                            if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                                event.setCancelled(true);
                            }
                            return;
                        }
                        Inventory topInventory = event.getView().getTopInventory();
                        if (event.getRawSlot() >= 0 && event.getRawSlot() <= type.getSlots()) {
                            if (inputs.containsKey(event.getRawSlot())) {
                                event.setCancelled(true);
                                Slot input = inputs.get(event.getRawSlot());
                                if (input instanceof Clicker clicker) {
                                    clicker.click(player, event.getView().getTopInventory(), event.getClick(), event.getAction());
                                } else if (input instanceof Storage storage) {
                                    Bukkit.getScheduler()
                                            .runTaskLater(IsekaiCore.Companion.getIsekaiCore(), () -> {
                                                ItemStack cursor = event.getCursor();
                                                if (cursor == null)
                                                    cursor = new ItemStack(Material.AIR);
                                                ItemStack storageItem = storage.getStored(player);
                                                if (cursor.getType() == Material.AIR) {
                                                    storage.setStored(player, null);
                                                    event.getView().setCursor(storageItem);
                                                    topInventory.setItem(event.getRawSlot(), null);
                                                } else {
                                                    if (storage.isAllow(player, cursor)) {
                                                        storage.setStored(player, cursor);
                                                        event.getView().setCursor(storageItem);
                                                        topInventory.setItem(event.getRawSlot(), cursor);
                                                    } else {
                                                        event.getView().setCursor(cursor);
                                                        topInventory.setItem(event.getRawSlot(), storageItem);
                                                    }
                                                }
                                            }, 1L);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (isDropped()) return;
        if (event.getPlayer() instanceof Player player) {
            if (cache.containsKey(player)) {
                if (event.getInventory().getHolder() instanceof NormalGUIHolder holder) {
                    if (holder.getId().equals(holders.get(player).getId())) {
                        holders.remove(player);
                        onClose.apply(player, cache.remove(player));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (isDropped()) return;
        cache.remove(event.getPlayer());
        holders.remove(event.getPlayer());
    }

    public enum Type {

        NORMAL_1X9(1),
        NORMAL_2X9(2),
        NORMAL_3X9(3),
        NORMAL_4X9(4),
        NORMAL_5X9(5),
        NORMAL_6X9(6);

        private final int lines;

        Type(int lines) {
            this.lines = lines;
        }

        public int getLines() {
            return lines;
        }

        public int getSlots() {
            return lines * 9;
        }

    }

}
