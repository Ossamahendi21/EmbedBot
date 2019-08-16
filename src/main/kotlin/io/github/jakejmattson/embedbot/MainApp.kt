package io.github.jakejmattson.embedbot

import me.aberrantfox.kjdautils.api.startBot

fun main(args: Array<String>) {
    val token = args.firstOrNull()
            ?: throw IllegalArgumentException("No program arguments provided. Expected bot token.")

    startBot(token) {
        configure {
            globalPath = "io.github.jakejmattson.embedbot"

            //Remove reaction on command invocation in favor of reaction on successful command execution
            reactToCommands = false

            //Move the help command from the internal "utility" category, to the local "Utility" category
            container.commands.getValue("help").category = "Utility"

            //Set the order that the categories will be sorted when generating documentation
            documentationSortOrder = arrayListOf("BotConfiguration", "GuildConfiguration", "Core", "Copy", "Field",
                "Cluster", "Edit", "Information", "Utility")
        }
    }
}