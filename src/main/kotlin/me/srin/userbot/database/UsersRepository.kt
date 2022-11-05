package me.srin.userbot.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UsersRepository : JpaRepository<Users, Users.ID> {
    @Query("""
        select u.`rank`
        from
            (select usr.user_id, usr.guild_id, rank() over (order by usr.total_xp desc) `rank` from users usr) u
        where u.user_id=?1 and u.guild_id=?2
    """, nativeQuery = true)
    fun getRank(userId: Long, guildId: Long): Int
}