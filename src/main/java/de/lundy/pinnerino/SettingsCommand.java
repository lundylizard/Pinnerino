package de.lundy.pinnerino;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        if (event.getName().equalsIgnoreCase("pinsettings")) {

            //event.get

            String webhookUrl = event.getOption("webhook").getAsString();
            //int amount = event.getOption("amount").getAsInt();

            event.reply(webhookUrl).queue();

        }

    }
}
