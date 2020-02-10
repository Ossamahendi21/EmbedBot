package me.jakejmattson.embedbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.jakejmattson.embedbot.dataclasses.Configuration

@Service
class PrefixService(private val configuration: Configuration, private val discord: Discord) {
    fun setPrefix(prefix: String) {
        configuration.prefix = prefix
        discord.configuration.prefix = prefix
    }
}