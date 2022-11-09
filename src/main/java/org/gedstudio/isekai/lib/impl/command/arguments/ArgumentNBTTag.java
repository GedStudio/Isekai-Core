package org.gedstudio.isekai.lib.impl.command.arguments;

import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.gedstudio.isekai.lib.open.command.EzArgumentType;

public class ArgumentNBTTag implements EzArgumentType {
    @Override
    public ArgumentType<?> type() {
        return net.minecraft.commands.arguments.ArgumentNBTTag.a();
    }

    @Override
    public Object get(CommandContext<?> context, String name) throws CommandSyntaxException {
        return JsonParser.parseString(net.minecraft.commands.arguments.ArgumentNBTTag.a(context, name).toString()).getAsJsonObject();
    }
}
