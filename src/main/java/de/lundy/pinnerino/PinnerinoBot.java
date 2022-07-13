package de.lundy.pinnerino;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.lundy.pinnerino.commands.AboutCommand;
import de.lundy.pinnerino.commands.PinSettingsCommand;
import de.lundy.pinnerino.commands.WebhookCommand;
import de.lundy.pinnerino.listeners.ReactionListener;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @author lundylizard
 */
public class PinnerinoBot {

    private static PinnerinoDatabase database;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        Gson gson = new Gson();
        JsonReader reader;

        try {
            reader = new JsonReader(new FileReader("config.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Read config.json into database class
        PinnerinoConfig config = gson.fromJson(reader, PinnerinoConfig.class);
        database = new PinnerinoDatabase(config.getUrl(), config.getUser(), config.getPassword());
        database.generateSettingsTable();
        database.generatePinnedTable();

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createLight(config.getToken())

                // Set required intents:
                // To access message content, attachments, ids, etc.
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGES)

                // To manage webhooks (Not sure if this is required, since the user creates one themselves)
                .setEnabledIntents(GatewayIntent.GUILD_WEBHOOKS)

                // To read reactions used for pinning
                .setEnabledIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)

                // Event Listeners:
                .addEventListeners(new ReactionListener())

                // Slash Commands:
                .addEventListeners(new PinSettingsCommand(), new WebhookCommand(), new AboutCommand())

                .setAutoReconnect(true);

        ShardManager shardManager;

        try {
            shardManager = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }

        shardManager.getShards().forEach(shard -> {

            CommandListUpdateAction commands = shard.updateCommands();

            // Remove all commands before adding. This removes obsolete and deleted commands.
            shard.retrieveCommands().complete().forEach(command -> command.delete().complete());

            commands.addCommands(

                Commands.slash("pinsettings", "Change settings regarding pins.")
                    .addOption(OptionType.INTEGER, "pinamount",
                        "Change amount required to pin a message.")
                    .addOption(OptionType.STRING, "pinemote",
                        "Change emote used to pin a message.")
                    .addOption(OptionType.BOOLEAN, "embedenabled",
                        "Toggle 'jump to message' embed.")
                    .addOption(OptionType.STRING, "embedcolor",
                        "Change the 'jump to message' embed color.")
                    .setGuildOnly(true)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("webhook", "Change the webhook url used to pin message.")
                    .addOption(OptionType.STRING, "url", "Webhook URL", true)
                    .setGuildOnly(true)
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),

                Commands.slash("about", "Display information about Pinnerino Bot")

            ).queue();

        });

        // This runs every 10 minutes. Updating the presence to "Playing on x servers"
        updatePresence(shardManager);

    }

    private static void updatePresence(ShardManager shardManager) {

        scheduler.scheduleWithFixedDelay(() -> {

            int serverCount = shardManager.getGuilds().size();
            String presenceContent = String.format("on %d servers", serverCount);
            shardManager.getShards().forEach(shard -> shard.getPresence()
                .setPresence(OnlineStatus.ONLINE, Activity.playing(presenceContent)));
            System.out.printf("Updated presence: %s%n", presenceContent);

        }, 2, 600, TimeUnit.SECONDS);

    }

    /**
     * Get the database instance.
     * @return Pinnerino Database instance
     */
    public static PinnerinoDatabase getDatabase() {
        return database;
    }

    public static class PinnerinoConfig {

        private final String token;
        private final String user;
        private final String url;
        private final String password;

        public PinnerinoConfig(String token, String user, String url, String password) {
            this.token = token;
            this.user = user;
            this.url = url;
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public String getUser() {
            return user;
        }

        public String getUrl() {
            return url;
        }

        public String getPassword() {
            return password;
        }

    }

}
