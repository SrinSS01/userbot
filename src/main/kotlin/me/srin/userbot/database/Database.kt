package me.srin.userbot.database

import me.srin.userbot.utils.Config
import org.springframework.stereotype.Component

@Component
class Database(
    val config: Config,
    val usersRepository: UsersRepository
) {
    companion object {
        val MEMBER_STATUS_LIST = hashMapOf<Long, Users.ID>()
    }
}