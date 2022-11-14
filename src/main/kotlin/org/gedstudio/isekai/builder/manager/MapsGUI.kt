package org.gedstudio.isekai.builder.manager

import org.bukkit.entity.Player
import org.gedstudio.isekai.core.IsekaiCore
import org.gedstudio.isekai.lib.open.gui.NormalGUI

object MapsGUI {

    fun openBrowser(player: Player) {
        val gui = NormalGUI(IsekaiCore.getIsekaiCore(), NormalGUI.Type.NORMAL_6X9)

    }

}