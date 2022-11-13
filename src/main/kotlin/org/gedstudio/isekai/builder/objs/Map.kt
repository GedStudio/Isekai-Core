package org.gedstudio.isekai.builder.objs

import net.deechael.genshin.lib.open.world.SlimeWorld
import org.gedstudio.isekai.builder.manager.IsekaiWorldManager
import java.util.UUID

class Map(val author: UUID, val name: String) {

    var world: SlimeWorld? = null
        private set

    fun isLoaded(): Boolean {
        return this.world != null
    }

    fun load() {
        this.world = IsekaiWorldManager.loadWorld(this.author, name)
    }

}