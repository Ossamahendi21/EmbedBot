package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.dataclasses.*
import io.github.jakejmattson.embedbot.extensions.*
import io.github.jakejmattson.embedbot.locale.messages
import io.github.jakejmattson.embedbot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.command.tryRetrieveSnowflake
import net.dv8tion.jda.api.entities.*

@CommandSet("Copy")
fun copyCommands(embedService: EmbedService) = commands {
    command("CopyTarget") {
        description = messages.descriptions.COPY_TARGET
        expect(arg(WordArg("Embed Name")),
                arg(TextChannelArg("Channel"), optional = true, default = { it.channel }),
                arg(WordArg("Message ID")))
        execute {
            val name = it.args.component1() as String
            val channel = it.args.component2() as TextChannel
            val messageId = it.args.component3() as String
            val guild = it.guild!!

            if (guild.hasEmbedWithName(name))
                return@execute it.respond(messages.errors.EMBED_ALREADY_EXISTS)

            val message = tryRetrieveSnowflake(it.discord.jda) {
                channel.retrieveMessageById(messageId.trimToID()).complete()
            } as Message? ?: return@execute it.respond("Could not find a message with that ID in the target channel.")

            val messageEmbed = message.getEmbed()
                ?: return@execute it.respond(messages.errors.NO_EMBED_IN_MESSAGE)

            val builder = messageEmbed.toEmbedBuilder()
            val embed = Embed(name, builder, CopyLocation(channel.id, messageId))

            embedService.addEmbed(guild, embed)

            it.reactSuccess()
        }
    }

    command("CopyPrevious") {
        description = messages.descriptions.COPY_PREVIOUS
        expect(arg(WordArg("Embed Name")),
                arg(TextChannelArg("Channel"), optional = true, default = { it.channel }))
        execute { event ->
            val name = event.args.component1() as String
            val channel = event.args.component2() as TextChannel
            val guild = event.guild!!
            val limit = 50

            if (guild.hasEmbedWithName(name))
                return@execute event.respond(messages.errors.EMBED_ALREADY_EXISTS)

            val previousMessages = channel.getHistoryBefore(event.message.id, limit).complete().retrievedHistory

            val previousEmbedMessage = previousMessages.firstOrNull { it.getEmbed() != null }
                ?: return@execute event.respond("No embeds found in the previous $limit messages.")

            val builder = previousEmbedMessage.getEmbed()!!.toEmbedBuilder()
            val previousEmbed = Embed(name, builder, CopyLocation(channel.id, previousEmbedMessage.id))

            embedService.addEmbed(guild, previousEmbed)

            event.reactSuccess()
        }
    }

    command("UpdateOriginal") {
        description = messages.descriptions.UPDATE_ORIGINAL
        requiresLoadedEmbed = true
        execute {
            val embed = it.guild!!.getLoadedEmbed()!!

            val original = embed.copyLocation
                ?: return@execute it.respond(messages.errors.NOT_COPIED)

            val updateResponse = embed.update(it.discord.jda, original.channelId, original.messageId)

            if (!updateResponse.wasSuccessful)
                return@execute it.respond(updateResponse.message)

            it.reactSuccess()
        }
    }

    command("UpdateTarget") {
        description = messages.descriptions.UPDATE_TARGET
        requiresLoadedEmbed = true
        expect(arg(TextChannelArg("Channel"), optional = true, default = { it.channel }),
                arg(WordArg("Message ID")))
        execute {
            val channel = it.args.component1() as TextChannel
            val messageId = it.args.component2() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            val updateResponse = embed.update(it.discord.jda, channel.id, messageId)

            if (!updateResponse.wasSuccessful)
                return@execute it.respond(updateResponse.message)

            it.reactSuccess()
        }
    }
}