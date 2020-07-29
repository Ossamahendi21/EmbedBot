package me.jakejmattson.embedbot.commands

import me.jakejmattson.embedbot.arguments.EmbedArg
import me.jakejmattson.embedbot.extensions.*
import me.jakejmattson.embedbot.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands
import me.jakejmattson.kutils.api.extensions.jda.sendPrivateMessage

@CommandSet("Meta")
fun metaCommands(embedService: EmbedService) = commands {
    command("Broadcast") {
        description = "Send a direct message to all guild owners."
        requiredPermissionLevel = Permission.BOT_OWNER
        execute(EveryArg) {
            val message = it.args.first

            it.discord.jda.guilds
                .mapNotNull { it.retrieveOwner().complete() }
                .distinct()
                .forEach {
                    it.user.sendPrivateMessage(message)
                }
        }
    }

    command("Transfer") {
        description = "Send an embed to another guild."
        requiredPermissionLevel = Permission.BOT_OWNER
        execute(EmbedArg("Embed").makeNullableOptional { it.guild?.getLoadedEmbed() },
            GuildArg("Target Guild"),
            AnyArg("New Name").makeNullableOptional { null }) {

            val (embed, target, newName) = it.args

            embed ?: return@execute it.respond("No embed selected or loaded.")

            embed.name = newName.takeUnless { it.isNullOrBlank() } ?: embed.name

            val hasCollision = target.getGuildEmbeds().embedList.any { it.name == embed.name }

            if (hasCollision)
                return@execute it.respond("An embed with this name already exists in ${target.name}")

            embedService.addEmbed(target, embed)
            it.reactSuccess()
        }
    }

    command("GuildReport") {
        description = "Get a complete list of guilds."
        requiredPermissionLevel = Permission.BOT_OWNER
        execute {
            val report = it.discord.jda.guilds.joinToString("\n") { it.id + " - " + it.name }
            it.respond("```$report```")
        }
    }
}