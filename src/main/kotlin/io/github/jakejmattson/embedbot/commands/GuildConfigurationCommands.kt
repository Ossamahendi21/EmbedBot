package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.arguments.RoleArg
import io.github.jakejmattson.embedbot.dataclasses.*
import io.github.jakejmattson.embedbot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import net.dv8tion.jda.core.entities.Role

@CommandSet("GuildConfiguration")
fun guildConfigurationCommands(configuration: Configuration,
                               persistenceService: PersistenceService,
                               embedService: EmbedService) = commands {
    command("SetRequiredRole") {
        requiresGuild = true
        description = "Set the role required to use this bot."
        expect(RoleArg)
        execute {
            val requiredRole = it.args.component1() as Role
            val guildConfiguration = configuration.getGuildConfig(it.guild!!.id)
                ?: return@execute it.respond("This guild is not set up for use. Please use the `setup` command.")


            guildConfiguration.requiredRole = requiredRole.name
            persistenceService.save(configuration)

            it.respond("Required role set to: ${requiredRole.name}")
        }
    }

    command("DeleteAll") {
        requiresGuild = true
        description = "Delete all embeds and clusters in this guild."
        execute {
            val guild = it.guild!!
            val removed = embedService.removeAllFromGuild(guild)

            it.respond("Successfully deleted $removed embeds.")
        }
    }

    command("Setup") {
        requiresGuild = true
        description = "Set up this bot for use."
        expect(RoleArg("Required Role"))
        execute {
            val requiredRole = it.args.component1() as Role
            val guildConfiguration = configuration.getGuildConfig(it.guild!!.id)

            if (guildConfiguration != null)
                return@execute it.respond("This guild is already setup for use.")

            configuration.guildConfigurations.add(GuildConfiguration(it.guild!!.id, requiredRole.name))
            persistenceService.save(configuration)

            it.respond("This guild is now setup for use!")
        }
    }
}