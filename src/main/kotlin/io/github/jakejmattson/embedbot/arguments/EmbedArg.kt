package io.github.jakejmattson.embedbot.arguments

import io.github.jakejmattson.embedbot.services.getGuildEmbeds
import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*

open class EmbedArg(override val name: String = "Embed Name") : ArgumentType {
    companion object : EmbedArg()

    override val examples = arrayListOf("")
    override val consumptionType = ConsumptionType.Single
    override fun convert(arg: String, args: List<String>, event: CommandEvent): ArgumentResult {
        val guild = event.guild ?: return ArgumentResult.Error("Must be invoked inside a guild.")

        val embed = getGuildEmbeds(guild.id).embedList.firstOrNull { it.name == arg }
            ?: return ArgumentResult.Error("No such embed exists with the name: $arg")

        return ArgumentResult.Single(embed)
    }
}