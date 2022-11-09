package org.gedstudio.isekai.lib.impl.command.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.gedstudio.isekai.lib.open.command.EzArgumentType;
import net.minecraft.commands.CommandListenerWrapper;

public class ArgumentUUID implements EzArgumentType {
    @Override
    public ArgumentType<?> type() {
        return net.minecraft.commands.arguments.ArgumentUUID.a();
    }

    @Override
    public Object get(CommandContext<?> context, String name) throws CommandSyntaxException {
        return net.minecraft.commands.arguments.ArgumentUUID.a((CommandContext<CommandListenerWrapper>) context, name);
    }
}
