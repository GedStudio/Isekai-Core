package org.gedstudio.isekai.core.util

import com.google.gson.JsonArray

object Json {

    fun array(vararg args: String) : JsonArray {
        val array = JsonArray()
        for (arg in args)
            array.add(arg)
        return array
    }

    fun allIn(vararg arrays: JsonArray): JsonArray {
        val newArray = JsonArray()
        for (array in arrays)
            newArray.addAll(array)
        return newArray
    }

}