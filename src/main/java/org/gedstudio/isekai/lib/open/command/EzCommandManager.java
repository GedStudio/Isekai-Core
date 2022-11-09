package org.gedstudio.isekai.lib.open.command;

import org.gedstudio.isekai.lib.impl.command.EzCommandManagerImpl;

public interface EzCommandManager {

    EzRegisteredCommand register(EzCommand ezCommand);

    EzRegisteredCommand register(String prefix, EzCommand ezCommand);

    static EzCommandManager getManager() {
        return EzCommandManagerImpl.INSTANCE;
    }

}
