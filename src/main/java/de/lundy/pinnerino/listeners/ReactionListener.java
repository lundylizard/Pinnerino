package de.lundy.pinnerino.listeners;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.lundy.pinnerino.PinnerinoBot;
import de.lundy.pinnerino.settings.PinnerinoSettingsDTO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        // Get settings from database
        PinnerinoSettingsDTO settings = PinnerinoBot.getDatabase().getSettingsFromDiscordId(event.getGuild().getIdLong());

        Message message = event.retrieveMessage().complete();

        // Get all registered pins from the database...
        List<Long> registeredPins = PinnerinoBot.getDatabase().getRegisteredPins();

        // ... and if the message is registered already, do nothing. This avoids duplicate pins.
        // Manually deleting a webhook message does not unregister the message, this is intentional.
        if (registeredPins.contains(message.getIdLong())) {
            return;
        }

        String reactionEmote = settings.getReactionEmote();
        int reactionAmount = settings.getReactionAmount();

        // Check if emote from reaction is the one specified in the database and the reaction amount is
        // greater or equal than the specified amount
        if (event.getEmoji().getName().equals(reactionEmote) &&
                event.getReaction().retrieveUsers().stream().count() >= reactionAmount) {

            // Get the needed webhook url from the settings dto
            String webhookUrl = settings.getWebhookUrl();

            // Create a webhook object using the webhook url specified in the database
            WebhookClient webhookClient = WebhookClient.withUrl(webhookUrl);
            WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();

            // Set the webhook username to the message author's username
            webhookBuilder.setUsername(message.getAuthor().getName());

            // Set the webhook avatar to the message author's avatar (profile picture)
            webhookBuilder.setAvatarUrl(message.getAuthor().getAvatarUrl());

            // Disallow all mentions, this avoids people mentioning everyone on the server using the webhook
            // despite having no permission to do so.
            webhookBuilder.setAllowedMentions(AllowedMentions.none());

            // List of attachments, in case there is any media attached to the message (images, videos, etc.)
            List<Message.Attachment> attachments = message.getAttachments();

            String messageContent = message.getContentRaw();

            // If there are attachments
            if (!attachments.isEmpty()) {

                attachments.forEach(attachment -> {

                    try {
                        // Add each attachment to the webhook builder
                        webhookBuilder.addFile(attachment.getFileName(), attachment.getProxy().download().get());
                    } catch (InterruptedException | ExecutionException e) {
                        // Reset files in case one file fails, I'd rather have no files showing in case one fails
                        webhookBuilder.resetFiles();
                        e.printStackTrace();
                    }
                });
            }

            // Set content of webhook builder to the message content. If there was no message attached,
            // send an invisible message, because the webhook content cannot be empty.
            webhookBuilder.setContent(messageContent.isEmpty() ? "_ _" : messageContent);

            // If the server has the jump embed enabled
            if (settings.isEmbedEnabled()) {

                // Create a MessageEmbed which has a link to jump to the pinned message
                MessageEmbed jumpEmbed = new EmbedBuilder()
                        .setColor(Color.decode(settings.getEmbedColor()))
                        .setDescription(String.format("[Jump to message](%s)", message.getJumpUrl()))
                        .build();

                // Add created embed to webhook
                webhookBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(jumpEmbed).build());

            }

            // Send and close the webhook
            webhookClient.send(webhookBuilder.build());
            webhookClient.close();
            webhookBuilder.reset();

            // Add pinned message to database to verify it was pinned (see top of method)
            PinnerinoBot.getDatabase().registerPin(message.getIdLong());

        }
    }

}
