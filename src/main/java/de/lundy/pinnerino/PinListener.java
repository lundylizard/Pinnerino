package de.lundy.pinnerino;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class PinListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        // TODO add setting for what emote is the pin emote
        if (event.getReaction().getReactionEmote().getEmoji().equals("\uD83D\uDCCC")) {

            //TODO add setting for how many reactions are necessary
            if (event.getReaction().retrieveUsers().stream().count() > 0L) {

                WebhookClient webhookClient = WebhookClient.withUrl("https://discord.com/api/webhooks/990294712810344468/3e6NOcrAGcE3f-gWlRkDhZhMadN6S00UPmGed7WzU2KW6wIB23fqvsHCKGgTbNyomTVD");
                WebhookMessageBuilder webhookBuilder = new WebhookMessageBuilder();

                StringBuilder message = new StringBuilder(event.retrieveMessage().complete().getContentRaw());
                List<Message.Attachment> attachments = event.retrieveMessage().complete().getAttachments();

                if (!attachments.isEmpty()) {
                    for (Message.Attachment attachment : attachments) {
                        message.append(attachment.getProxy().getUrl()).append(" ");
                    }
                }

                webhookBuilder.setContent(message.toString());
                webhookBuilder.setUsername(event.retrieveMessage().complete().getAuthor().getName());
                webhookBuilder.setAvatarUrl(event.retrieveMessage().complete().getAuthor().getAvatarUrl());
                webhookBuilder.setAllowedMentions(AllowedMentions.none());
                webhookClient.send(webhookBuilder.build());
                webhookClient.close();

            }

        }

    }
}
