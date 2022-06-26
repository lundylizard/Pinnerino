package de.lundy.pinnerino;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PinListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        if (event.getReaction().getReactionEmote().getEmoji().equals(PinnerinoBot.getDatabase().getReactionEmoteFromId(event.getGuild().getId())) &&
                event.getReaction().retrieveUsers().stream().count() >= PinnerinoBot.getDatabase().getPinAmountFromId(event.getGuild().getId())) {

                WebhookClient webhookClient = WebhookClient.withUrl(PinnerinoBot.getDatabase().getWebhookFromId(event.getGuild().getId()));
                WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();
                List<Message.Attachment> attachments = event.retrieveMessage().complete().getAttachments();

                if (!attachments.isEmpty()) {
                    for (Message.Attachment attachment : attachments) {
                        try {
                            webhookBuilder.addFile(attachment.getFileName(), attachment.getProxy().download().get());
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                String messageContent = event.retrieveMessage().complete().getContentRaw();

                if (!messageContent.isEmpty()) {
                    webhookBuilder.setContent(messageContent);
                } else {
                    webhookBuilder.setContent("_ _");
                }

                webhookBuilder.setUsername(event.retrieveMessage().complete().getAuthor().getName());
                webhookBuilder.setAvatarUrl(event.retrieveMessage().complete().getAuthor().getAvatarUrl());
                webhookBuilder.setAllowedMentions(AllowedMentions.none());
                MessageEmbed jumpEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription("[Click to jump to message](" + event.retrieveMessage().complete().getJumpUrl() + ")")
                    .build();
                webhookBuilder.addEmbeds(WebhookEmbedBuilder.fromJDA(jumpEmbed).build());
                webhookClient.send(webhookBuilder.build());
                webhookClient.close();

        }

    }
}
