package org.gedstudio.isekai.lib.sql

import java.lang.RuntimeException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

abstract class SqlHelper(url: String) {

    private val url: String

    init {
        this.url = url
    }

    private var connection: Connection? = null

    fun prepare(sql: String): PreparedStatement {
        if (this.connection == null)
            throw RuntimeException("The sql hasn't been connected yet!")
        return this.connection!!.prepareStatement(sql)
    }

    fun connect() {
        if (this.connection != null && !this.connection!!.isClosed)
            throw RuntimeException("The connection has been created already!")
        this.connection = DriverManager.getConnection(this.url)
    }

    fun close() {
        if (this.connection == null)
            throw RuntimeException("The connection has been closed already!")
        if (this.connection!!.isClosed)
            throw RuntimeException("The connection has been closed already!")
        this.connection!!.close()
        this.connection = null
    }

    fun isClose(): Boolean {
        return this.connection == null || this.connection!!.isClosed
    }

}