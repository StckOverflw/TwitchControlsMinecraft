package net.stckoverflw.twitchcontrols

import net.axay.fabrik.core.text.literal
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Formatting
import net.stckoverflw.twitchcontrols.command.mainCommand
import net.stckoverflw.twitchcontrols.minecraft.EventManager
import net.stckoverflw.twitchcontrols.minecraft.twitch.TwitchEventsClient
import net.stckoverflw.twitchcontrols.util.twitchChannel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*

val twitchEventsClients = hashMapOf<UUID, TwitchEventsClient?>()

val twitchControlsLogger: Logger = LogManager.getLogger(MOD_ID)

fun createTwitchClient(player: PlayerEntity): TwitchEventsClient? {
    twitchEventsClients[player.uuid]?.close()
    twitchEventsClients[player.uuid] = null
    val twitchChannel = player.twitchChannel
    return if (twitchChannel != null) {
        twitchEventsClients[player.uuid] = TwitchEventsClient(player, twitchChannel)
        twitchEventsClients[player.uuid]
    } else {
        null
    }
}

class TwitchControlsMod : ModInitializer, DedicatedServerModInitializer {

    override fun onInitialize() {
        mainCommand
    }

    override fun onInitializeServer() {
        EventManager()
        registerEvents()
    }

    private fun registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, _, _ ->
            if (twitchEventsClients[handler.player.uuid] == null && createTwitchClient(handler.player) == null) {
                handler.player.sendMessage("Couldn't connect twitch".literal.formatted(Formatting.RED), false)
            } else {
                handler.player.sendMessage("Connected to twitch".literal.formatted(Formatting.GREEN), false)
            }
        })
    }


}