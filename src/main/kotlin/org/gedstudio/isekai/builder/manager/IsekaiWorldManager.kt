package org.gedstudio.isekai.builder.manager

import net.deechael.genshin.lib.open.world.DataSource
import net.deechael.genshin.lib.open.world.SlimeWorld
import net.deechael.genshin.lib.open.world.WorldManager
import org.gedstudio.isekai.core.IsekaiCore
import java.util.UUID

object IsekaiWorldManager {

    fun loadWorld(owner: UUID, name: String): SlimeWorld {
        return WorldManager.getManager().load(IsekaiCore.getIsekaiCore(), DataSource.FILE, "${owner}-$name")
    }

}