package org.gedstudio.isekai.lib.open.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerInvoker {

    int execute(Player sender, EzArgument argument) throws CommandSyntaxException;

}
