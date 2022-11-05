package me.srin.userbot.utils

import me.srin.userbot.database.Database
import me.srin.userbot.database.Users
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

object Utils {
    val EXECUTOR: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(2)

    fun Users.startCoolDownAndTrain(database: Database, channelId: Long, trainingCallback: (msg: String) -> Unit, errorCallback: (msg: String) -> Unit) {
        val currentTime = System.currentTimeMillis() / 1000
        val trainingCooldownInSeconds = database.config.trainingCooldownInSeconds
        if ((currentTime - cooldown) <= trainingCooldownInSeconds) {
            errorCallback(":stopwatch: Oh your are still training in <#$trainingChannel>, wait until you can use this command again at <t:${cooldown + trainingCooldownInSeconds}:T>")
        } else {
            val periodInSeconds: Long = trainingCooldownInSeconds * 1000 / (xpLimit / trainingCount)
            cooldown = currentTime
            trainingChannel = channelId
            val scheduledFuture =
                EXECUTOR.scheduleWithFixedDelay({
                    train()
                    database.usersRepository.save(this)
                }, 0, periodInSeconds, TimeUnit.MILLISECONDS)
            database.usersRepository.save(this)
            trainingCallback("pet started training in <#$channelId>")
            EXECUTOR.schedule({ scheduledFuture.cancel(true) }, trainingCooldownInSeconds, TimeUnit.SECONDS)
        }
    }
    fun Users.getStats(config: Config) =
         StringBuilder().run {
            append("https://user-bot-next-app.vercel.app/api/og?")
                .append("xp=").append(xp).append('&')
                .append("xp-limit=").append(xpLimit).append('&')
                .append("color=").append(config.statusColor).append('&')
                .append("level=").append(level).append('&')
                .append("rank=").append(rank).append('&')
                .append("background=").append(URLEncoder.encode(config.statusBackground, StandardCharsets.UTF_8)).append('&')
                .append("name=" + URLEncoder.encode(name, StandardCharsets.UTF_8)).append('&')
                .append("profile=" + URLEncoder.encode(pfp, StandardCharsets.UTF_8))
        }.toString()
}