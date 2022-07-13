package de.lundy.pinnerino;

import de.lundy.pinnerino.exception.PinnerinoDatabaseException;
import de.lundy.pinnerino.settings.PinnerinoSettingsDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PinnerinoDatabase {

    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    public PinnerinoDatabase(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public Connection getConnection() throws SQLException {

        if (this.connection != null) {
            return this.connection;
        }

        Properties properties = new Properties();
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());
        properties.setProperty("MaxPooledStatements", "250");
        properties.setProperty("autoReconnect", "true");
        this.connection = DriverManager.getConnection(getUrl() + "?autoReconnect=true", properties);

        return this.connection;

    }

    public void generateSettingsTable() {

        Statement statement;

        try {

            statement = getConnection().createStatement();
            statement.execute("create table if not exists settings " +
                    "(discordId bigint(20) not null primary key, " +
                    "webhookUrl text, " +
                    "reactionAmount int default 10, " +
                    "reactionEmote text default '\uD83D\uDCCC', " +
                    "embedEnabled bool default true, " +
                    "embedColor text default '#ff0000')");
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PinnerinoDatabaseException("Could not create 'settings' table.");
        }

    }

    public void generatePinnedTable() {

        Statement statement;

        try {

            statement = getConnection().createStatement();
            statement.execute("create table if not exists pinned (messageId bigint(20) not null)");
            statement.close();

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not create 'pinned' table.");
        }

    }

    /** Pins start here **/

    public void registerPin(long messageId) {

        PreparedStatement statement;

        try {

            statement = getConnection().prepareStatement("insert into pinned values (?)");
            statement.setLong(1, messageId);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not register pinned message with ID %s (%s)", messageId, e.getMessage());
        }

    }

    public List<Long> getRegisteredPins() {

        Statement statement;

        try {

            statement = getConnection().createStatement();
            statement.execute("select messageId from pinned");
            ResultSet results = statement.getResultSet();
            List<Long> registeredPins = new ArrayList<>();

            while (results.next()) {
                registeredPins.add(results.getLong("messageId"));
            }

            results.close();
            statement.close();

            return registeredPins;

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not get registered pins (%s)", e.getMessage());
        }

    }

    /** Settings start here **/

    public PinnerinoSettingsDTO getSettingsFromDiscordId(long discordId) {

        PinnerinoSettingsDTO settingsDto = new PinnerinoSettingsDTO();
        PreparedStatement statement;

        try {

            statement = getConnection().prepareStatement("select * from settings where discordId=?");
            statement.setLong(1, discordId);
            statement.execute();
            ResultSet results = statement.getResultSet();

            while (results.next()) {

                settingsDto.setDiscordId(discordId);
                settingsDto.setWebhookUrl(results.getString("webhookUrl"));
                settingsDto.setEmbedColor(results.getString("embedColor"));
                settingsDto.setEmbedEnabled(results.getBoolean("embedEnabled"));
                settingsDto.setReactionAmount(results.getInt("reactionAmount"));
                settingsDto.setReactionEmote(results.getString("reactionEmote"));

            }

            results.close();
            statement.close();

            return settingsDto;

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not get settings from server with ID %s (%s)", discordId, e.getMessage());
        }

    }

    public void setWebhook(long discordId, String webhookUrl) {

        PreparedStatement statement;

        try {

            statement = getConnection().prepareStatement("insert into settings (discordId, webhookUrl) values (?, ?) " +
                "on duplicate key update webhookUrl=?");

            statement.setLong(1, discordId);
            statement.setString(2, webhookUrl);
            statement.setString(3, webhookUrl);

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not set webhook of server with ID %d (%s)", discordId, e.getMessage());
        }

    }

    public void createOrUpdateSettings(PinnerinoSettingsDTO settings) {

        PreparedStatement statement;

        try {

            statement = getConnection().prepareStatement("insert into settings values (?, ?, ?, ?, ?, ?) " +
                "on duplicate key update " +
                "webhookUrl=?, " +
                "reactionAmount=?, " +
                "reactionEmote=?, " +
                "embedEnabled=?, " +
                "embedColor=?");

            statement.setLong(1, settings.getDiscordId());
            statement.setString(2, settings.getWebhookUrl());
            statement.setInt(3, settings.getReactionAmount());
            statement.setString(4, settings.getReactionEmote());
            statement.setBoolean(5, settings.isEmbedEnabled());
            statement.setString(6, settings.getEmbedColor());
            statement.setString(7, settings.getWebhookUrl());
            statement.setInt(8, settings.getReactionAmount());
            statement.setString(9, settings.getReactionEmote());
            statement.setBoolean(10, settings.isEmbedEnabled());
            statement.setString(11, settings.getEmbedColor());

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            throw new PinnerinoDatabaseException("Could not create or update settings of server with ID %s (%s)", settings.getDiscordId(), e.getMessage());
        }

    }

}
