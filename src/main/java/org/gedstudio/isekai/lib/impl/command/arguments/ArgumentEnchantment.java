package org.gedstudio.isekai.lib.impl.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.gedstudio.isekai.lib.open.command.EzArgumentType;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.craftbukkit.v1_19_R1.enchantments.CraftEnchantment;

public class ArgumentEnchantment implements EzArgumentType {
    @Override
    public ArgumentType<?> type() {
        return net.minecraft.commands.arguments.ArgumentEnchantment.a();
    }

    @Override
    public Object get(CommandContext<?> context, String name) throws CommandSyntaxException {
        return new CraftEnchantment(net.minecraft.commands.arguments.ArgumentEnchantment.a((CommandContext<CommandListenerWrapper>) context, name));
    }
}
