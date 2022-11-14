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
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.gedstudio.isekai.core.IsekaiCore;
import org.gedstudio.isekai.lib.impl.gui.FakeAnvil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AnvilGUI implements Listener {

    private static Class<?> anvilClass;

    static {
        anvilClass = generateAnvilClass();
    }

    private final Plugin plugin;
    private final Map<Player, Inventory> cache = new HashMap<>();
    private final Map<Integer, Slot> inputs = new HashMap<>();
    private final String title;
    private final Map<Player, String> outputString = new HashMap<>();
    private boolean dropped = false;
    private DuParameter<Player, Inventory> onClose = (player, inventory) -> {
    };

    public AnvilGUI(Plugin plugin) {
        this(plugin, "Anvil");
    }

    public AnvilGUI(Plugin plugin, String title) {
        if (anvilClass == null) {
            anvilClass = generateAnvilClass();
        }
        this.plugin = plugin;
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

    public boolean hasItem(AnvilSlot slot) {
        if (isDropped()) return false;
        return inputs.containsKey(slot.getSlot());
    }

    public Slot getItem(AnvilSlot slot) {
        if (isDropped()) throw new RuntimeException("This GUI has been dropped!");
        if (!hasItem(slot)) throw new RuntimeException("The slot is empty");
        return inputs.get(slot.getSlot());
    }

    public void setItem(AnvilSlot slot, Slot input) {
        if (isDropped()) return;
        if (slot == AnvilSlot.OUTPUT) {
            if (input instanceof AnvilOutputSlot anvilOutputSlot) {
                setOutput(anvilOutputSlot);
            }
            return;
        }
        inputs.put(slot.getSlot(), input);
        for (Map.Entry<Player, Inventory> entry : cache.entrySet()) {
            if (input instanceof Image image) {
                entry.getValue().setItem(slot.getSlot(), image.draw(entry.getKey(), entry.getValue()));
            }
        }
    }

    public void setOutput(AnvilOutputSlot input) {
        if (isDropped()) return;
        inputs.put(2, input);
        for (Map.Entry<Player, Inventory> entry : cache.entrySet()) {
            if (input instanceof AnvilOutputImage image) {
                entry.getValue().setItem(2, image.draw(entry.getKey(), entry.getValue(), outputString.get(entry.getKey())));
            }
        }
    }

    public Slot remove(AnvilSlot slot) {
        if (isDropped()) throw new RuntimeException("The GUI has been dropped!");
        return inputs.remove(slot.getSlot());
    }

    public Inventory getBukkit(Player player) {
        if (isDropped() || !cache.containsKey(player))
            throw new RuntimeException("The gui which player is watching is not this gui!");
        return cache.get(player);
    }

    public void open(Player player) {
        if (isDropped()) return;
        Class<?> clazz = anvilClass;
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(Player.class);
            constructor.setAccessible(true);
            AnvilInterface anvil = (AnvilInterface) constructor.newInstance(player);
            Inventory inventory = anvil.castToBukkit();
            for (int i = 0; i < 3; i++) {
                Slot input = inputs.get(i);
                if (input == null)
                    continue;
                if (input instanceof Image image) {
                    inventory.setItem(i, image.draw(player, inventory));
                } else if (input instanceof AnvilOutputImage image) {
                    if (inputs.containsKey(0)) {
                        ItemMeta itemMeta = inventory.getItem(0).getItemMeta();
                        if (itemMeta != null) {
                            outputString.put(player, itemMeta.getDisplayName());
                        } else {
                            outputString.put(player, "");
                        }
                        inventory.setItem(2, image.draw(player, inventory, outputString.get(player)));
                    }
                }
            }
            anvil.open(title);
            cache.put(player, inventory);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
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
    public void onAnvil(PrepareAnvilEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (cache.containsKey(player)) {
            if (event.getView().getTopInventory().equals(cache.get(player))) {
                ItemStack result = event.getResult();
                if (result != null) {
                    outputString.put(player, result.getItemMeta().getDisplayName());
                }
                if (inputs.containsKey(2)) {
                    Slot input = inputs.get(2);
                    if (input instanceof AnvilOutputImage image) {
                        event.setResult(image.draw(player, event.getView().getTopInventory(), outputString.get(player)));
                    }
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////
    //////                Player Listener                //////
    ///////////////////////////////////////////////////////////

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (isDropped()) return;
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (cache.containsKey(player)) {
                if (event.getView().getTopInventory().equals(cache.get(player))) {
                    if (Objects.equals(event.getClickedInventory(), event.getView().getTopInventory())) {
                        event.setCancelled(true);
                    } else {
                        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                            event.setCancelled(true);
                        }
                        return;
                    }
                    Inventory topInventory = event.getView().getTopInventory();
                    if (event.getRawSlot() >= 0 && event.getRawSlot() <= 2) {
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
                            } else if (input instanceof AnvilOutputClicker clicker) {
                                Inventory inventory = event.getView().getTopInventory();
                                clicker.click(player, inventory, outputString.get(player), event.getClick(), event.getAction());
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
                if (event.getView().getTopInventory().equals(cache.get(player))) {
                    outputString.remove(player);
                    onClose.apply(player, cache.remove(player));
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (isDropped()) return;
        cache.remove(event.getPlayer());
        outputString.remove(event.getPlayer());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////                                     Dynamic Class Generator                                       ///////
    ///////             2022 DCG Project - https://www.github.com/DeeChael/DynamicClassGenerator              ///////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static Class<?> generateAnvilClass() {
        return FakeAnvil.class;
    }

    ///////////////////////////////////////////////////////////////////////////////
    /////////                           Enums                             /////////
    ///////////////////////////////////////////////////////////////////////////////

    public enum AnvilSlot {
        LEFT_INPUT(0), RIGHT_INPUT(1), OUTPUT(2);

        private final int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }

}
