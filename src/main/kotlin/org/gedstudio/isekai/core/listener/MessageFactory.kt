package org.gedstudio.isekai.core.listener

import org.gedstudio.isekai.lib.open.protocol.LightInjector
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.netty.channel.Channel
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import org.bukkit.entity.Player
import org.gedstudio.isekai.core.IsekaiCore
import org.gedstudio.isekai.core.util.Json
import org.gedstudio.isekai.core.util.Msg

object MessageFactory : LightInjector(IsekaiCore.getIsekaiCore()) {

    override fun onPacketReceiveAsync(sender: Player?, channel: Channel, packet: Any): Any {
        return packet
    }

    override fun onPacketSendAsync(receiver: Player?, channel: Channel, packet: Any): Any? {
        if (packet is ClientboundSystemChatPacket) {
            val locale = receiver?.locale()?.toString()?.lowercase() ?: "en_us"
            val clientboundSystemChatPacket = packet
            var solved: JsonObject
            try {
                // If the server is paper based, enter this structure
                val clazz = ClientboundSystemChatPacket::class.java
                val field = clazz.getDeclaredField("adventure\$content")
                field.isAccessible = true
                val component = field.get(clientboundSystemChatPacket)
                if (component == null && clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = if (clientboundSystemChatPacket.content != null)
                    solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject, locale)
                else
                    solveObject(Msg.asJsonObject(component as Component), locale)
                // println(clientboundSystemChatPacket.content)
                // println(if (component == null) null else Msg.asJson(component as Component))
            } catch (e: NoSuchFieldException) {
                // Throw exception or error, so the server is spigot or craftbukkit
                if (clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject, locale)
            } catch (e: NoSuchFieldError) {
                // Throw exception or error, so the server is spigot or craftbukkit
                if (clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject, locale)
            }

            return try {
                // If the server is spigot or craftbukkit
                ClientboundSystemChatPacket(
                    Msg.GSON.toJson(solved),
                    clientboundSystemChatPacket.c()
                )
            } catch (e: NoSuchMethodException) {
                // Throw exception or error, so the server is paper based
                val clazz = ClientboundSystemChatPacket::class.java
                val constructor = clazz.getConstructor(Component::class.java, Boolean::class.java)
                constructor.newInstance(Msg.fromJson(solved), clientboundSystemChatPacket.c())
            } catch (e: NoSuchMethodError) {
                // Throw exception or error, so the server is paper based
                val clazz = ClientboundSystemChatPacket::class.java
                val constructor = clazz.getConstructor(Component::class.java, Boolean::class.java)
                // println(solved)
                constructor.newInstance(Msg.fromJson(solved), clientboundSystemChatPacket.c())
            }
        }
        return packet
    }

    private fun solveObject(obj: JsonObject, locale: String): JsonObject {
        var newObj = JsonObject()
        if (obj.has("text") && obj.get("text").isJsonPrimitive) {
            val text = obj.getAsJsonPrimitive("text")
            if (text.isString) {
                val format = text.asString
                if (format.startsWith("isekai.") && Msg.has(format)) {
                    newObj = JsonParser.parseString(Msg.asJson(format, locale)).asJsonObject
                } else {
                    newObj = obj.deepCopy()!!
                    newObj.remove("extra")
                }
            } else {
                newObj = obj.deepCopy()!!
                newObj.remove("extra")
            }
        } else {
            newObj = obj.deepCopy()!!
            newObj.remove("extra")
        }
        if (obj.has("extra") && obj.get("extra").isJsonArray) {
            val newArray = JsonArray()
            for (element in obj.getAsJsonArray("extra")) {
                if (element !is JsonObject) {
                    newArray.add(element)
                    continue
                }
                val jsonObject = element.asJsonObject
                newArray.add(solveObject(jsonObject, locale))
            }
            if (newArray.size() > 0) {
                if (newObj.has("extra") && newObj.get("extra").isJsonArray && newObj.getAsJsonArray("extra").size() > 0) {
                    val oldExtra = newObj.getAsJsonArray("extra")
                    newObj.remove("extra")
                    newObj.add("extra", Json.allIn(oldExtra, newArray))
                } else
                    newObj.add("extra", newArray)
            }
        }
        return newObj
    }

    fun init() = Unit

}