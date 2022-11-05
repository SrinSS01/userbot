package me.srin.userbot.utils

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties
@ConfigurationProperties("bot")
@Getter @Setter
class Config {
    lateinit var token: String
    var trainingCooldownInSeconds: Long = 0
    lateinit var statusBackground: String
    lateinit var trainingChannels: List<Long>
    lateinit var statusColor: String
}