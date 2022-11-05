package me.srin.userbot

import lombok.AllArgsConstructor
import me.srin.userbot.database.Database
import me.srin.userbot.database.MemberJoin
import me.srin.userbot.events.ButtonPress
import me.srin.userbot.events.GuildReady
import me.srin.userbot.events.SlashCommand
import me.srin.userbot.utils.Utils
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@AllArgsConstructor
class Main(private val database: Database): CommandLineRunner {
    override fun run(vararg args: String?) {
        val token = database.config.token
        LOGGER.info("Started bot with token: {}", token)
        JDABuilder.createDefault(token)
            .enableIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                GatewayIntent.GUILD_VOICE_STATES
            )
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .enableCache(CacheFlag.CLIENT_STATUS)
            .disableCache(
                CacheFlag.EMOJI,
                CacheFlag.STICKER,
                CacheFlag.VOICE_STATE
            )
            .setStatus(OnlineStatus.ONLINE)
            .addEventListeners(
                ButtonPress(database),
                MemberJoin(database),
                GuildReady(database),
                SlashCommand(database)
            ).build().run {
                Utils.EXECUTOR.scheduleWithFixedDelay({
                    if (readln() == "stop") {
                        Utils.EXECUTOR.shutdownNow()
                        shutdownNow()
                    }
                }, 0, 1, TimeUnit.SECONDS)
            }
    }
}