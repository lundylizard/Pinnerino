package de.lundy.pinnerino;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        // TODO figure this out
        if (event.getName().equalsIgnoreCase("pinsettings")) {

            OptionMapping webhook = event.getOption("webhook");
            OptionMapping amount = event.getOption("amount");

        }

    }
}
