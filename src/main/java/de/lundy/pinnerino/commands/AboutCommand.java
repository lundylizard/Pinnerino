package de.lundy.pinnerino.commands;

import de.lundy.pinnerino.PinnerinoBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class AboutCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // Create new embed builder
        EmbedBuilder embedBuilder = new EmbedBuilder();

        // Get description builder from embed
        StringBuilder embedContent = embedBuilder.getDescriptionBuilder();

        // Get total of pinned messages
        int pinnedAmount = PinnerinoBot.getDatabase().getRegisteredPins().size();

        // Set content of embed
        embedBuilder.setTitle("About Pinnerino Bot", "https://github.com/lundylizard/Pinnerino");
        embedContent.append("Pinnerino Bot (beta)\n");
        embedContent.append("Developed by [lundylizard](https://github.com/lundylizard/)\n\n");
        embedContent.append("https://github.com/lundylizard/Pinnerino\n\n");
        embedContent.append("Total pinned messages: `").append(pinnedAmount).append("`");
        embedBuilder.setDescription(embedContent);
        embedBuilder.setColor(0xff0000);

        // Send the embed
        event.replyEmbeds(embedBuilder.build()).queue();

        // Clear the embed builder
        embedBuilder.clear();

    }
}
