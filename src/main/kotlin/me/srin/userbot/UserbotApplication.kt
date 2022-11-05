package me.srin.userbot

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.io.FileWriter

@SpringBootApplication
class UserbotApplication

val LOGGER: Logger = LoggerFactory.getLogger(UserbotApplication::class.java)

fun main(args: Array<String>) {
    File("config").run {
        if (!exists()) {
            val result = mkdirs()
            if (result) {
                LOGGER.info("Created config directory")
                val properties = File("config/application.yml")
                if (!properties.exists()) {
                    FileWriter(properties).use { writer ->
                        writer.write(
                            """
                        bot:
                          token: token
                          training-cooldown-in-seconds: 720
                          status-background: null
                          status-color: yellow
                          training-channels:
                            - channel id 1
                            - channel id 2
                          
                        database:
                          host: "localhost:3306"
                          name: "name"
                          user: "user"
                          password: "password"
                    """.trimIndent()
                        )
                        writer.flush()
                        LOGGER.info("Created application.yml file")
                    }
                }
            } else {
                LOGGER.error("Failed to create config directory")
            }
            return
        }
    }
    runApplication<UserbotApplication>(*args)
}
