package org.gedstudio.isekai.lib.impl.gui;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.gedstudio.isekai.lib.open.gui.AnvilInterface;

public class FakeAnvil extends ContainerAnvil implements AnvilInterface {

    private final Player bukkitPlayer;

    public FakeAnvil(Player player) {
        super(((CraftPlayer) player).getHandle().nextContainerCounter(),
                ((CraftPlayer) player).getHandle().fA(),
                ContainerAccess.a(((CraftWorld) player.getWorld()).getHandle(),
                        new BlockPosition(0, 0, 0)));
        this.checkReachable = false;
        this.bukkitPlayer = player;
    }

    @Override
    public void l() {
        this.w.a(0);
    }

    @Override
    public void b(EntityHuman entityhuman) {
    }

    @Override
    protected void a(EntityHuman entityhuman, IInventory iinventory) {
        super.a(entityhuman, iinventory);
    }

    @Override
    public void open(String title) {
        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(this.getWindowId(),
                Containers.h, IChatBaseComponent.b(title));
        ((CraftPlayer) this.bukkitPlayer).getHandle().b.a(packet);
        ((CraftPlayer) this.bukkitPlayer).getHandle().bU = this;
        ((CraftPlayer) this.bukkitPlayer).getHandle().a(this);
    }

    @Override
    public Inventory castToBukkit() {
        return this.getBukkitView().getTopInventory();
    }

    @Override
    public int getWindowId() {
        return this.j;
    }

}
