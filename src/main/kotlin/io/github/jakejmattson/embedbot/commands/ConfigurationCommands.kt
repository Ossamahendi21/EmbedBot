package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.arguments.RoleArg
import io.github.jakejmattson.embedbot.data.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import net.dv8tion.jda.core.entities.Role

@CommandSet("Configuration")
fun configurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {
    command("SetRequiredRole") {
        requiresGuild = true
        description = "Set the role required to use this bot."
        expect(RoleArg)
        execute {
            val requiredRole = it.args.component1() as Role
            val guildConfiguration = configuration.getGuildConfig(it.guild!!.id)

            if (guildConfiguration == null)
                configuration.guildConfigurations.add(GuildConfiguration(it.guild!!.id, requiredRole.name))
            else
                guildConfiguration.requiredRole = requiredRole.name

            persistenceService.save(configuration)
        }
    }
}