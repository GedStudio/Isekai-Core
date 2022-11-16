package org.gedstudio.isekai.builder.manager

import org.gedstudio.isekai.builder.objs.Map
import org.gedstudio.isekai.core.IsekaiCore
import org.gedstudio.isekai.lib.sql.SqliteHelper
import java.io.File
import java.sql.PreparedStatement

object PlayersMapManager {

    private var sqlite: SqliteHelper? = null

    private val UPLOADED_MAPS = "CREATE TABLE `uploaded_map` IF NOT EXISTS ('player_uuid' TEXT, 'map_uuid' TEXT, 'map_name' TEXT, 'description' TEXT);"

    fun save(map: Map) {
    }

    fun isUploaded(map: Map): Boolean {
        return false
    }

    fun init() {
        sqlite = SqliteHelper(File(IsekaiCore.getIsekaiCore().dataFolder, "data.db"))
        var prepared = sqlite!!.prepare(UPLOADED_MAPS)
        executeUpdate(prepared)
        prepared = sqlite!!.prepare("")
        executeUpdate(prepared)
    }

    private fun executeUpdate(preparedStatement: PreparedStatement) {
        preparedStatement.executeUpdate()
        preparedStatement.close()
    }

}