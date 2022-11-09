package org.gedstudio.isekai.lib.open.command;

import com.mojang.brigadier.tree.CommandNode;

public interface EzRegisteredCommand {

    CommandNode<?> asBrigadier();

    EzCommand getCommand();

}
