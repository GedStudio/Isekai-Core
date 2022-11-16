package org.gedstudio.isekai.lib.sql

import java.io.File

class SqliteHelper(file: File): SqlHelper("jdbc:sqlite:" + file.path) {

    init {
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
    }

}