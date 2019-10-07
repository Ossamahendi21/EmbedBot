package io.github.jakejmattson.embedbot.services

import com.google.gson.Gson
import io.github.jakejmattson.embedbot.dataclasses.Configuration
import io.github.jakejmattson.embedbot.extensions.requiredPermissionLevel
import io.github.jakejmattson.embedbot.locale.messages
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.*
import net.dv8tion.jda.api.entities.*
import java.awt.Color

@Service
class StartupService(configuration: Configuration,
                     discord: Discord,
                     validationService: ValidationService,
                     prefixService: PrefixService,
                     permissionsService: PermissionsService) {
    private data class Properties(val version: String, val author: String, val repository: String)
    private val propFile = Properties::class.java.getResource("/properties.json").readText()
    private val project = Gson().fromJson(propFile, Properties::class.java)

    init {
        with(validationService.validateConfiguration()) {
            require(wasSuccessful) { message }
            println(message)
        }

        prefixService.setPrefix(configuration.prefix)

        discord.configuration.mentionEmbed = {
            val guild = it.guild

            embed {
                val self = guild.jda.selfUser
                val requiredRole = configuration.getGuildConfig(guild.id)?.requiredRole ?: "<Not Configured>"

                color = Color(0x00bfff)
                thumbnail = self.effectiveAvatarUrl
                addField(self.fullName(), messages.descriptions.BOT)
                addInlineField("Required role", requiredRole)
                addInlineField("Prefix", configuration.prefix)
                addInlineField("Author", "[${project.author}](${messages.links.DISCORD_ACCOUNT})")
                addInlineField("Version", project.version)
                addInlineField("Source", project.repository)
            }
        }

        discord.configuration.visibilityPredicate = predicate@{ command: Command, user: User, _: MessageChannel, guild: Guild? ->
            guild ?: return@predicate false

            val member = user.toMember(guild)!!
            val permission = command.requiredPermissionLevel

            permissionsService.hasClearance(member, permission)
        }
    }
}