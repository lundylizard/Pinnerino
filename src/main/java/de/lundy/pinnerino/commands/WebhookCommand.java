package de.lundy.pinnerino.commands;

import club.minnced.discord.webhook.WebhookClient;
import de.lundy.pinnerino.PinnerinoBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class WebhookCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getGuild() == null) {
            return;
        }

        if (event.getName().equalsIgnoreCase("webhook")) {

            OptionMapping webhookUrlOption = event.getOption("url");

            if (webhookUrlOption != null) {

                if (!webhookUrlOption.getAsString().matches(WebhookClient.WEBHOOK_URL)) {
                    event.reply("You did not provide a valid webhook url. " +
                        "If you have trouble, please look at the setup guide:\n" +
                        "<https://github.com/lundylizard/Pinnerino/README.md#Setup>").setEphemeral(true).queue();
                    return;
                }

                String webhookUrl = webhookUrlOption.getAsString();
                long discordId = event.getGuild().getIdLong();

                PinnerinoBot.getDatabase().setWebhook(discordId, webhookUrl);
                event.reply(String.format("Successfully set webhook url to (hidden): ||%s||", webhookUrl))
                    .setEphemeral(true).queue();

            }

        }

    }
}
