package org.gedstudio.isekai.core.registry

import org.bukkit.Bukkit
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

object PermissionRegistry {

    fun init() {
        Bukkit.getPluginManager().addPermission(Permission("isekai.op", PermissionDefault.FALSE))
    }

}