package de.lundy.pinnerino;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() throws SQLException {

        if (connection != null) {
            return connection;
        }

        Properties properties = new Properties();
        properties.setProperty("user", getUser());
        properties.setProperty("password", getPassword());
        properties.setProperty("MaxPooledStatements", "250");
        properties.setProperty("autoReconnect", "true");
        connection = DriverManager.getConnection(getUrl(), properties);
        return connection;

    }

    public String getReactionEmoteFromId(String id) {
        System.out.println(id);
        return "\uD83D\uDCCC";
    }

    public long getPinAmountFromId(String id) {
        System.out.println(id);
        return 0L;
    }

    public String getWebhookFromId(String id) {
        System.out.println(id);
        return "https://discord.com/api/webhooks/990294712810344468/3e6NOcrAGcE3f-gWlRkDhZhMadN6S00UPmGed7WzU2KW6wIB23fqvsHCKGgTbNyomTVD";
    }
}
