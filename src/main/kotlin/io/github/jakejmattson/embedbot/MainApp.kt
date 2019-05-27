package io.github.jakejmattson.embedbot

import io.github.jakejmattson.embedbot.utilities.generateDocs
import me.aberrantfox.kjdautils.api.*
import me.aberrantfox.kjdautils.api.dsl.*

private lateinit var commands: CommandsContainer

fun main(args: Array<String>) {
    val token = args.first()

    startBot(token) {
        configure {
            prefix = ">"
            globalPath = "io.github.jakejmattson.embedbot"

            commands = this@startBot.container
        }
    }

    println(generateDocs(commands))
}