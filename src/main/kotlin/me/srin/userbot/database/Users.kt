@file:Suppress("unused")

package me.srin.userbot.database

import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "users")
@IdClass(Users.ID::class)
class Users {
    @Id
    var userId: Long = 0

    @Id
    var guildId: Long = 0
    lateinit var name: String

    @Column(columnDefinition = "int default 0")
    var level = 0

    @Column(columnDefinition = "int default 0")
    var xp = 0

    @Column(columnDefinition = "int default 100")
    var xpLimit = 100

    @Column(columnDefinition = "int default 0")
    var totalXp = 0

    @Column(columnDefinition = "bigint default 0")
    var cooldown: Long = 0

    @Column(columnDefinition = "bigint default 0")
    var trainingChannel: Long = 0

    @Column(columnDefinition = "bigint default 2")
    var trainingCount: Long = 2

    @Transient
    var rank = 0

    @Transient
    lateinit var pfp: String
    fun train() {
        xp++
        totalXp++
        if (xp >= xpLimit) {
            xp = xpLimit - xp
            level++
            xpLimit = level * 423
            trainingCount *= 2
        }
    }

    @NoArgsConstructor
    @EqualsAndHashCode
    class ID(var userId: Long, var guildId: Long) : Serializable {
        constructor(): this(0, 0)
    }
}