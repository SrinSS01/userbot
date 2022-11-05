package me.srin.userbot.events

import me.srin.userbot.database.Database
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GuildReady(database: Database): Events(database) {
    private val logger: Logger = LoggerFactory.getLogger(GuildReady::class.java)
    override fun onGuildReady(event: GuildReadyEvent) {
        event.guild.run {
            logger.info("Guild ready: $name")
            updateCommands().addCommands(
                Commands.slash("train", "start training"),
                Commands.slash("view-stats", "view your training status"),
                Commands.slash("view-stats-user", "view training status of a certain user")
                    .addOption(OptionType.USER, "user", "user who's status will be shown", true)
            ).queue()
            members.forEach { it.insertIfNotPresent() }
        }
    }
}