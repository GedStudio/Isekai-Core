package org.gedstudio.isekai.core.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.gedstudio.isekai.core.IsekaiCore
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object Msg {

    val SERIALIZER = GsonComponentSerializer.gson()

    val GSON = Gson()

    private var text: JsonObject = JsonObject()

    private fun getMsg(format: String): String {
        if (text.get(format).isJsonArray) {
            val strings = ArrayList<String>()
            for (element in text.getAsJsonArray(format))
                strings.add(element.asString)
            return strings.joinToString("<newline>")
        }
        return text.get(format).asString
    }

    fun mini(): MiniMessage {
        return MiniMessage.miniMessage()
    }

    fun has(format: String): Boolean {
        return text.has(format)
    }

    fun get(format: String): Component {
        return mini().deserialize(getMsg(format))
    }

    fun asJson(component: Component): String {
        return SERIALIZER.serialize(component)
    }

    fun asJsonObject(component: Component): JsonObject {
        return SERIALIZER.serializeToTree(component).asJsonObject
    }

    fun asJson(format: String): String {
        return asJson(get(format))
    }

    fun fromJson(element: JsonElement): Component {
        return SERIALIZER.deserializeFromTree(element)
    }

    private fun initLanguage(file: File) {
        val parent: File = file.parentFile
        parent.mkdirs()
        file.createNewFile()
        val default = JsonObject()
        default.addProperty("isekai.message.test", "This is a test isekai message!")
        default.addProperty("isekai.message.unknown-command", "<red><bold>(!) <reset><red>Unknown command! Type \"/help\" to get help!")

        val fileWriter = FileWriter(file)
        fileWriter.write(GSON.toJson(default))
        fileWriter.close()
    }

    fun init() {
        val languageFile: File = File(IsekaiCore.getIsekaiCore().dataFolder, "language/en_us.json")
        if (!languageFile.exists())
            initLanguage(languageFile)
        this.text = JsonParser.parseReader(FileReader(languageFile)).asJsonObject
    }

}