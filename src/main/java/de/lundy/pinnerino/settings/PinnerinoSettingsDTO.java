package de.lundy.pinnerino.settings;

import java.util.Objects;

public class PinnerinoSettingsDTO {

    private long discordId;
    private String webhookUrl;
    private int reactionAmount;
    private String reactionEmote;
    private boolean embedEnabled;
    private String embedColor;

    public long getDiscordId() {
        return discordId;
    }

    public void setDiscordId(long discordId) {
        this.discordId = discordId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public int getReactionAmount() {
        return reactionAmount;
    }

    public void setReactionAmount(int reactionAmount) {
        this.reactionAmount = reactionAmount;
    }

    public String getReactionEmote() {
        return reactionEmote;
    }

    public void setReactionEmote(String reactionEmote) {
        this.reactionEmote = reactionEmote;
    }

    public boolean isEmbedEnabled() {
        return embedEnabled;
    }

    public void setEmbedEnabled(boolean embedEnabled) {
        this.embedEnabled = embedEnabled;
    }

    public String getEmbedColor() {
        return embedColor;
    }

    public void setEmbedColor(String embedColor) {
        this.embedColor = embedColor;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PinnerinoSettingsDTO that = (PinnerinoSettingsDTO) o;
        if (discordId != that.discordId) return false;
        if (reactionAmount != that.reactionAmount) return false;
        if (embedEnabled != that.embedEnabled) return false;
        if (!Objects.equals(webhookUrl, that.webhookUrl)) return false;
        if (!Objects.equals(reactionEmote, that.reactionEmote)) return false;
        return Objects.equals(embedColor, that.embedColor);

    }

    @Override
    public int hashCode() {

        int result = (int) (discordId ^ (discordId >>> 32));
        result = 31 * result + (webhookUrl != null ? webhookUrl.hashCode() : 0);
        result = 31 * result + reactionAmount;
        result = 31 * result + (reactionEmote != null ? reactionEmote.hashCode() : 0);
        result = 31 * result + (embedEnabled ? 1 : 0);
        result = 31 * result + (embedColor != null ? embedColor.hashCode() : 0);
        return result;

    }

    @Override
    public String toString() {
        return "PinnerinoSettingsDTO{" + "discordId=" + discordId + ", webhookUrl='" + webhookUrl + '\'' + ", reactionAmount=" + reactionAmount + ", reactionEmote='" + reactionEmote + '\'' + ", embedEnabled=" + embedEnabled + ", embedColor='" + embedColor + '\'' + '}';
    }

}
