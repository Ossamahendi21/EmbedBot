package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.arguments.RoleArg
import io.github.jakejmattson.embedbot.data.*
import io.github.jakejmattson.embedbot.services.PrefixService
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.arguments.WordArg
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import net.dv8tion.jda.core.entities.Role

@CommandSet("GuildConfiguration")
fun guildConfigurationCommands(configuration: Configuration, persistenceService: PersistenceService) = commands {
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

            it.respond("Required role set to: ${requiredRole.name}")
        }
    }
}

@CommandSet("BotConfiguration")
fun botConfigurationCommands(configuration: Configuration, prefixService: PrefixService, persistenceService: PersistenceService) = commands {
    command("SetPrefix") {
        requiresGuild = true
        description = "Set the prefix required for the bot to register a command."
        expect(WordArg("Prefix"))
        execute {
            val prefix = it.args.component1() as String

            prefixService.setPrefix(prefix)
            persistenceService.save(configuration)

            it.respond("Prefix set to: $prefix")
        }
    }
}