package me.srin.userbot.events

import me.srin.userbot.database.Database
import me.srin.userbot.database.Users
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.hooks.ListenerAdapter

abstract class Events(private val database: Database): ListenerAdapter() {
    protected fun Member.insertIfNotPresent() {
        if (user.isBot) {
            return
        }
        val usersRepository = database.usersRepository
        val member = usersRepository.findById(Users.ID(idLong, guild.idLong))
        if (member.isEmpty) {
            val users = Users()
            users.userId = idLong
            users.guildId = guild.idLong
            users.name = effectiveName
            usersRepository.save(users)
        }
    }
}