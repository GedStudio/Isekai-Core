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

    private var lang: MutableMap<String, JsonObject> = HashMap()

    private fun getMsg(format: String, locale: String = "en_us"): String {
        if (lang.containsKey(locale.lowercase())) {
            val text = lang[locale.lowercase()]!!
            if (text.get(format).isJsonArray) {
                val strings = ArrayList<String>()
                for (element in text.getAsJsonArray(format))
                    strings.add(element.asString)
                return strings.joinToString("<newline>")
            }
            return text.get(format).asString
        } else {
            if (text.get(format).isJsonArray) {
                val strings = ArrayList<String>()
                for (element in text.getAsJsonArray(format))
                    strings.add(element.asString)
                return strings.joinToString("<newline>")
            }
            return text.get(format).asString
        }
    }

    fun mini(): MiniMessage {
        return MiniMessage.miniMessage()
    }

    fun has(format: String): Boolean {
        return text.has(format)
    }

    fun get(format: String, locale: String = "en_us"): Component {
        return mini().deserialize(getMsg(format, locale.lowercase()))
    }

    fun asJson(component: Component): String {
        return SERIALIZER.serialize(component)
    }

    fun asJsonObject(component: Component): JsonObject {
        return SERIALIZER.serializeToTree(component).asJsonObject
    }

    fun asJson(format: String, locale: String = "en_us"): String {
        return asJson(get(format, locale.lowercase()))
    }

    fun fromJson(element: JsonElement): Component {
        return SERIALIZER.deserializeFromTree(element)
    }

    private fun initLanguage(file: File) {
        val parent: File = file.parentFile
        parent.mkdirs()
        file.createNewFile()
        val default = JsonObject()
        default.add("isekai.message.introduction", Json
            .array("",
                "",
                "       <green>Welcome to <yellow>Lingod Isekai<green>!",
                "       <green>This is a fun server made by only one person!",
                "       <gray><italic>(Tips: Enter \"/help\" to get help!)",
                "",
                ""
            ))
        default.add("isekai.message.command.help", Json
            .array("<green>Welcome to <yellow>Lingod Isekai<green>!"
            ))
        default.addProperty("isekai.message.command.isekai-reload.success", "<green><bold>(!) <reset><green>Reloaded the configuration files of isekai successfully!")
        default.addProperty("isekai.message.command.plugins", "<blue><bold>(!) <reset><blue>Plugins (<yellow>1<blue>): <green>Isekai-Core")
        default.addProperty("isekai.message.unknown-command", "<red><bold>(!) <reset><red>Unknown command! Type \"/help\" to get help!")

        val fileWriter = FileWriter(file, Charsets.UTF_8)
        fileWriter.write(GSON.toJson(default))
        fileWriter.close()
    }

    fun reload() {
        val languageFile = File(IsekaiCore.getIsekaiCore().dataFolder, "language/en_us.json")
        if (!languageFile.exists())
            initLanguage(languageFile)
        this.text = JsonParser.parseReader(FileReader(languageFile)).asJsonObject
        val languageFolder = File(IsekaiCore.getIsekaiCore().dataFolder, "language")
        if (!languageFolder.exists())
            return
        val files = languageFolder.listFiles() ?: return
        for (file in files) {
            if (file == null)
                continue
            var langName = file.name
            if (!langName.endsWith(".json"))
                continue
            langName = langName.substring(0, langName.length - 5)
            this.lang[langName] = JsonParser.parseReader(FileReader(file, Charsets.UTF_8)).asJsonObject
        }
    }

}