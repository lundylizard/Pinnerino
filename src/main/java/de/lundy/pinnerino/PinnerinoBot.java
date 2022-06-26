package de.lundy.pinnerino;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PinnerinoBot {

    private static PinnerinoDatabase database;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws LoginException, IOException {

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader("config.json"));

        PinnerinoConfig config = gson.fromJson(reader, PinnerinoConfig.class);
        database = new PinnerinoDatabase(config.getUrl(), config.getUser(), config.getPassword());

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createLight(config.getToken());
        builder.setEnabledIntents(GatewayIntent.GUILD_MESSAGES);
        builder.setEnabledIntents(GatewayIntent.GUILD_WEBHOOKS);
        builder.setEnabledIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS);

        builder.addEventListeners(new PinListener());
        builder.addEventListeners(new SettingsCommand());
        builder.setAutoReconnect(true);

        ShardManager shardManager = builder.build();

        shardManager.getShards().forEach((shard) -> {

            CommandListUpdateAction commands = shard.updateCommands();

            commands.addCommands(
                    Commands.slash("pinsettings", "Change Pinnerino's settings.")
                            .addOption(OptionType.STRING, "webhook", "Set the webhook url.")
                            .addOption(OptionType.INTEGER, "amount", "Required amount of reactions.")
            );

            commands.queue();

        });

        tick(shardManager);

    }

    private static void tick(ShardManager shardManager) {

        scheduler.scheduleWithFixedDelay(() -> {

            System.out.println(System.currentTimeMillis() + " - tick");
            int serverCount = shardManager.getGuilds().size();
            shardManager.getShards().forEach(shard -> shard.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing("on " + serverCount + " servers.")));

        }, 1, 5, TimeUnit.MINUTES);

    }

    public static PinnerinoDatabase getDatabase() {
        return database;
    }

    public static class PinnerinoConfig {

        private String token;
        private String user;
        private String url;
        private String password;

        public PinnerinoConfig(String token, String user, String url, String password) {
            this.token = token;
            this.user = user;
            this.url = url;
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
