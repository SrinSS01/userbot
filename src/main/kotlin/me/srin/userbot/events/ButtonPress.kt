package me.srin.userbot.events

import me.srin.userbot.database.Database
import me.srin.userbot.database.Users
import me.srin.userbot.utils.Utils.startCoolDownAndTrain
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ButtonPress(private val database: Database): Events(database) {
//    private val logger = LoggerFactory.getLogger(ButtonPress::class.java)
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val member = event.member ?: return
        val uid = Users.ID(member.idLong, event.guild!!.idLong)
        val idLong = event.messageIdLong
        val id = Database.MEMBER_STATUS_LIST[idLong]
        if (id == null || (id.userId != uid.userId)) {
            event.deferEdit().queue()
            return
        }
        when (event.button.id) {
            "train" -> {
                val usersOptional = database.usersRepository.findById(id)
                usersOptional.ifPresentOrElse (
                    { user ->
                        user.startCoolDownAndTrain(database, event.channel.idLong,
                        {
                            event.reply(it).setEphemeral(true).queue()
                        }) { msg -> event.reply(msg).setEphemeral(true).queue() }
                    }, {
                        event.deferEdit().queue()
                    }
                )
            }
        }
    }
}