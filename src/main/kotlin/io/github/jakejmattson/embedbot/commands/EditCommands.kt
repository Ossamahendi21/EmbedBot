package io.github.jakejmattson.embedbot.commands

import io.github.jakejmattson.embedbot.arguments.EmbedArg
import io.github.jakejmattson.embedbot.dataclasses.Embed
import io.github.jakejmattson.embedbot.extensions.*
import io.github.jakejmattson.embedbot.services.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import net.dv8tion.jda.api.entities.User
import java.time.LocalDateTime

private const val commandDescriptionFormat = "Set the %s for the currently loaded embed."

@CommandSet("Edit")
fun editCommands() = commands {
    command("SetAuthor") {
        description = commandDescriptionFormat.format("author")
        requiresLoadedEmbed = true
        expect(UserArg)
        execute {
            val user = it.args.component1() as User
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setAuthor(user.name, user.effectiveAvatarUrl)
            it.reactSuccess()
        }
    }

    command("SetColor") {
        description = commandDescriptionFormat.format("color")
        requiresLoadedEmbed = true
        expect(HexColorArg)
        execute {
            val color = it.args.component1() as Int
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setColor(color)
            it.reactSuccess()
        }
    }

    command("SetDescription") {
        description = commandDescriptionFormat.format("description")
        requiresLoadedEmbed = true
        expect(SentenceArg)
        execute {
            val description = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            if (description.length > DESCRIPTION_LIMIT)
                return@execute it.respond("Max description length is $DESCRIPTION_LIMIT characters. Input was ${description.length}.")

            embed.setDescription(description)
            it.reactSuccess()
        }
    }

    command("SetFooter") {
        description = commandDescriptionFormat.format("footer")
        requiresLoadedEmbed = true
        expect(UrlArg("Icon URL"), SentenceArg("Text"))
        execute {
            val url = it.args.component1() as String
            val text = it.args.component2() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            if (text.length > FOOTER_LIMIT)
                return@execute it.respond("Max footer length is $FOOTER_LIMIT characters. Input was ${text.length}.")

            embed.setFooter(text, url)
            it.reactSuccess()
        }
    }

    command("SetImage") {
        description = commandDescriptionFormat.format("image")
        requiresLoadedEmbed = true
        expect(UrlArg)
        execute {
            val url = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setImage(url)
            it.reactSuccess()
        }
    }

    command("SetThumbnail") {
        description = commandDescriptionFormat.format("thumbnail")
        requiresLoadedEmbed = true
        expect(UrlArg)
        execute {
            val url = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setThumbnail(url)
            it.reactSuccess()
        }
    }

    command("SetTimestamp") {
        description = commandDescriptionFormat.format("timestamp")
        requiresLoadedEmbed = true
        execute {
            val embed = it.guild!!.getLoadedEmbed()!!

            embed.setTimestamp(LocalDateTime.now())
            it.reactSuccess()
        }
    }

    command("SetTitle") {
        description = commandDescriptionFormat.format("title")
        requiresLoadedEmbed = true
        expect(SentenceArg)
        execute {
            val title = it.args.component1() as String
            val embed = it.guild!!.getLoadedEmbed()!!

            if (title.length > TITLE_LIMIT)
                return@execute it.respond("Max title limit is $TITLE_LIMIT characters. Input was ${title.length}.")

            embed.setTitle(title)
            it.reactSuccess()
        }
    }

    command("Clear") {
        description = "Clear a target field from the loaded embed."
        requiresLoadedEmbed = true
        expect(arg(WordArg("Clear Target"), optional = true, default = ""))
        execute {
            val field = (it.args.component1() as String).toLowerCase()
            val embed = it.guild!!.getLoadedEmbed()!!
            val options = "Options:\nAuthor, Color, Description, Footer, Image, Thumbnail, Timestamp, Title \nAll, Fields, Non-Fields"

            with (embed) {
                when (field) {
                    "author" -> clearAuthor()
                    "color" -> clearColor()
                    "description" -> clearDescription()
                    "footer" -> clearFooter()
                    "image" -> clearImage()
                    "thumbnail" -> clearThumbnail()
                    "timestamp" -> clearTimestamp()
                    "title" -> clearTitle()

                    "all" -> clear()
                    "fields" -> clearFields()
                    "non-fields" -> clearNonFields()
                    else -> null
                }
            } ?: return@execute it.respond("Invalid field selected. $options")

            it.reactSuccess()
        }
    }

    command("Rename") {
        description = "Change the name of an existing embed."
        expect(arg(EmbedArg, optional = true, default = { it.guild!!.getLoadedEmbed() }), arg(WordArg("New Name")))
        execute {
            val targetEmbed = it.args.component1() as Embed?
                ?: return@execute it.respond("Please load an embed or specify one explicitly.")

            val newName = it.args.component2() as String
            val guild = it.guild!!

            if (guild.hasEmbedWithName(newName))
                return@execute it.respond("An embed with this name already exists.")

            if (targetEmbed.isLoaded(guild)) {
                val updatedEmbed = guild.getEmbedByName(targetEmbed.name)!!
                updatedEmbed.name = newName
                guild.loadEmbed(updatedEmbed)
            }
            else {
                targetEmbed.name = newName
            }

            it.reactSuccess()
        }
    }
}