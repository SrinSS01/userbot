package me.srin.userbot.database

import me.srin.userbot.events.Events
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

class MemberJoin(database: Database): Events(database) {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        event.member.insertIfNotPresent()
    }
}