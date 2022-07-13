package de.lundy.pinnerino.commands;

import de.lundy.pinnerino.PinnerinoBot;
import de.lundy.pinnerino.settings.PinnerinoSettingsDTO;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class PinSettingsCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        // I have no idea how this would be the case.
        if (event.getMember() == null) {
            event.reply("An unknown error occurred. Please try again later.")
                .setEphemeral(true).queue();
            return;
        }

        if (event.getGuild() == null) {
            return;
        }

        if (event.getName().equalsIgnoreCase("pinsettings")) {

            String subcommandName = event.getSubcommandName();

            if ("pinamount".equals(subcommandName)) {

                OptionMapping pinAmountOption = event.getOption("pinamount");

                if (pinAmountOption == null) {
                    return;
                }

                int pinAmount = pinAmountOption.getAsInt();

                if (pinAmount < 1) {
                    event.reply(":warning: Amount can't be lower than 1.\n" +
                        "Setting this value to 1 pins a message immediately upon reaction.")
                        .setEphemeral(true).queue();
                    return;
                }

                PinnerinoSettingsDTO settings = new PinnerinoSettingsDTO();

                settings.setDiscordId(event.getGuild().getIdLong());
                settings.setReactionAmount(pinAmount);

                PinnerinoBot.getDatabase().createOrUpdateSettings(settings);
                event.reply(String.format("Updated required reaction amount to `%d`", pinAmount))
                    .setEphemeral(true).queue();

            } else if ("pinemote".equals(subcommandName)) {

                OptionMapping pinEmoteOption = event.getOption("pinemote");

                if (pinEmoteOption == null) {
                    return;
                }

                String pinEmote = pinEmoteOption.getAsString();

                // TODO add proper emote check - not sure how yet lol
                if (!isValidEmote(pinEmote)) {
                    return;
                }

                PinnerinoSettingsDTO settings2 = new PinnerinoSettingsDTO();

                settings2.setDiscordId(event.getGuild().getIdLong());
                settings2.setReactionEmote(pinEmote);

                PinnerinoBot.getDatabase().createOrUpdateSettings(settings2);
                event.reply(String.format("Updated required reaction emote to %s `%s`", pinEmote, pinEmote))
                    .setEphemeral(true).queue();
            }

        }
    }

    public boolean isValidEmote(String emote) {
        return true;
    }

}
