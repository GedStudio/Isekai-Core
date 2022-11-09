package org.gedstudio.isekai.core.listener

import com.fren_gor.lightInjector.LightInjector
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.netty.channel.Channel
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import org.bukkit.entity.Player
import org.gedstudio.isekai.core.IsekaiCore
import org.gedstudio.isekai.core.util.Msg

object MessageFactory : LightInjector(IsekaiCore.getIsekaiCore()) {

    override fun onPacketReceiveAsync(sender: Player?, channel: Channel, packet: Any): Any {
        return packet
    }

    override fun onPacketSendAsync(receiver: Player?, channel: Channel, packet: Any): Any? {
        if (packet is ClientboundSystemChatPacket) {
            val clientboundSystemChatPacket = packet
            var solved: JsonObject
            try {
                val clazz = ClientboundSystemChatPacket::class.java
                val field = clazz.getDeclaredField("adventure\$content")
                field.isAccessible = true
                val component = field.get(clientboundSystemChatPacket)
                if (component == null && clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = if (clientboundSystemChatPacket.content != null)
                    solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject)
                else
                    solveObject(Msg.asJsonObject(component as Component))
            } catch (e: NoSuchFieldException) {
                if (clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject)
            } catch (e: NoSuchFieldError) {
                if (clientboundSystemChatPacket.content == null)
                    return clientboundSystemChatPacket
                solved = solveObject(JsonParser.parseString(clientboundSystemChatPacket.content!!).asJsonObject)
            }

            return try {
                ClientboundSystemChatPacket(
                    Msg.GSON.toJson(solved),
                    clientboundSystemChatPacket.c()
                )
            } catch (e: NoSuchMethodException) {
                val clazz = ClientboundSystemChatPacket::class.java
                val constructor = clazz.getConstructor(Component::class.java, Boolean::class.java)
                constructor.newInstance(Msg.fromJson(solved), clientboundSystemChatPacket.c())
            } catch (e: NoSuchMethodError) {
                val clazz = ClientboundSystemChatPacket::class.java
                val constructor = clazz.getConstructor(Component::class.java, Boolean::class.java)
                constructor.newInstance(Msg.fromJson(solved), clientboundSystemChatPacket.c())
            }
        }
        return packet
    }

    private fun solveObject(obj: JsonObject): JsonObject {
        var newObj = JsonObject()
        if (obj.has("text") && obj.get("text").isJsonPrimitive) {
            val text = obj.getAsJsonPrimitive("text")
            if (text.isString) {
                val format = text.asString
                if (format.startsWith("isekai.message.") && Msg.has(format)) {
                    obj.remove("text")
                    newObj = JsonParser.parseString(Msg.asJson(format)).asJsonObject
                } else {
                    newObj.add("text", text)
                }
            }
        }
        val newArray = JsonArray()
        if (obj.has("extra") && obj.get("extra").isJsonArray) {
            for (element in obj.getAsJsonArray("extra")) {
                if (element !is JsonObject) {
                    newArray.add(element)
                    continue
                }
                val jsonObject = element.asJsonObject
                newArray.add(solveObject(jsonObject))
            }
        }
        if (newArray.size() > 0)
            newObj.add("extra", newArray)
        return newObj
    }

    fun init() = Unit

}