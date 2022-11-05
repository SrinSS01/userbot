package me.srin.userbot.events

import me.srin.userbot.database.Database
import me.srin.userbot.database.Users
import me.srin.userbot.utils.Utils.getStats
import me.srin.userbot.utils.Utils.startCoolDownAndTrain
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SlashCommand(private val database: Database): Events(database) {
    private val logger: Logger = LoggerFactory.getLogger(SlashCommand::class.java)
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        var member = event.member ?: let {
            event.reply("Bot must be used in a guild!!!").setEphemeral(true).queue()
            return
        }
        val trainingChannels = database.config.trainingChannels
        val isTrainingChannel = trainingChannels.contains(event.channel.idLong)
        val id = Users.ID(member.idLong, event.guild!!.idLong)
        when (event.name) {
            "train" -> {
                logger.info("training")
                if (!isTrainingChannel) {
                    event.reply("You can only train in the training channels")
                        .setEphemeral(true)
                        .queue()
                    return
                }
                val usersOptional = database.usersRepository.findById(id)
                usersOptional.ifPresentOrElse(
                    { user ->
                        user.startCoolDownAndTrain(database, event.channel.idLong,
                        { success ->
                            event.reply(success).setEphemeral(true).queue()
                        }) { msg -> event.reply(msg).setEphemeral(true).queue() }
                    }, { event.deferReply(true).queue() }
                )
            }
            "view-stats", "view-stats-user" -> {
                event.deferReply().queue()
                var isAdmin = false
                event.getOption("user")?.let {
                    isAdmin = true
                    member = it.asMember ?: let {
                        event.reply("User not found").setEphemeral(true).queue()
                        return
                    }
                }
                val mID = Users.ID(member.idLong, event.guild!!.idLong)
                val users = database.usersRepository.findById(mID)
                if (users.isEmpty) {
                    return
                }
                val user = users.get()
                val rank = database.usersRepository.getRank(mID.userId, mID.guildId)
                user.rank = rank
                user.pfp = member.effectiveAvatarUrl
                val url = user.getStats(database.config)

                if (!isAdmin && isTrainingChannel) {
                    event.hook.editOriginal(url)
                        .setActionRow(Button.primary("train", "Train"))
                        .queue { message ->
                            Database.MEMBER_STATUS_LIST[message.idLong] = Users.ID(member.idLong, event.guild!!.idLong)
                            message.editMessageComponents(
                                ActionRow.of(Button.primary("train", "Train").asDisabled())
                            ).queueAfter(3, TimeUnit.MINUTES) { m -> Database.MEMBER_STATUS_LIST.remove(m.idLong) }
                        }
                } else {
                    event.hook.editOriginal(url).queue()
                }
            }
        }
    }
}