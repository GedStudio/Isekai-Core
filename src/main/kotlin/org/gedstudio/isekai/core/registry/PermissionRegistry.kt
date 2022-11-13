package org.gedstudio.isekai.core.registry

import org.bukkit.Bukkit
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

object PermissionRegistry {

    fun init() {
        no("isekai.op")
    }

    private fun no(permission: String) {
        Bukkit.getPluginManager().addPermission(Permission(permission, PermissionDefault.FALSE))
    }

    private fun yes(permission: String) {
        Bukkit.getPluginManager().addPermission(Permission(permission, PermissionDefault.TRUE))
    }

    private fun op(permission: String) {
        Bukkit.getPluginManager().addPermission(Permission(permission, PermissionDefault.OP))
    }

}