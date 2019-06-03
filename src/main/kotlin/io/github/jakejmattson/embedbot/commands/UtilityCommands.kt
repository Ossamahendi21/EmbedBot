package io.github.jakejmattson.embedbot.commands

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import java.util.Date

private data class Properties(val version: String, val author: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val Project = Gson().fromJson(propFile, Properties::class.java)
private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands() = commands {
    command("Ping") {
        requiresGuild = true
        description = "Display the network ping of the bot."
        execute {
            it.respond("JDA ping: ${it.jda.ping}ms\n")
        }
    }

    command("Version") {
        requiresGuild = true
        description = "Display the bot version."
        execute {
            it.respond("**Running version**: ${Project.version}")
        }
    }

    command("Author") {
        requiresGuild = true
        description = "Display project author."
        execute {
            it.respond("**Project author**: ${Project.author}")
        }
    }

    command("Source") {
        requiresGuild = true
        description = "Display the (source code) repository link."
        execute {
            it.respond(Project.repository)
        }
    }

    command("BotInfo") {
        requiresGuild = true
        description = "Display the bot information."
        execute {
            it.respond(embed {
                title(it.jda.selfUser.fullName())
                description("A Discord embed management bot.")
                setColor(Color.green)
                setThumbnail(it.jda.selfUser.effectiveAvatarUrl)
                addField("Author", Project.author, false)
                addField("Source", Project.repository, false)
                addField("Version", Project.version, false)
            })
        }
    }

    command("Uptime") {
        requiresGuild = true
        description = "Displays how long the bot has been running."
        execute {
            val milliseconds = Date().time - startTime.time
            val seconds = (milliseconds / 1000) % 60
            val minutes = (milliseconds / (1000 * 60)) % 60
            val hours = (milliseconds / (1000 * 60 * 60)) % 24
            val days = (milliseconds / (1000 * 60 * 60 * 24))

            it.respond(embed {
                setColor(Color.WHITE)
                setTitle("I have been running since")
                setDescription(startTime.toString())

                field {
                    name = "That's been"
                    value = "$days day(s), $hours hour(s), $minutes minute(s) and $seconds second(s)"
                }
            })
        }
    }

    command("ListCommands") {
        requiresGuild = true
        description = "List all available commands."
        execute {
            val commands = it.container.commands.values.groupBy { it.category }.toList()
                .sortedBy { (_, value) -> -value.size }.toMap()

            it.respond(embed {
                commands.forEach {
                    field {
                        name = it.key
                        value = it.value.sortedBy { it.name }.joinToString("\n") { it.name }
                        inline = true
                    }
                }
                setColor(Color.green)
            })
        }
    }
}