package de.lundy.pinnerino;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class PinnerinoBot {

    public static void main(String[] args) throws LoginException {

        JDABuilder builder = JDABuilder.createDefault("ODM3MDc3MTE2NTQ1NTMxOTc0.YInS0A.LmPuPxEntNFtk8rj8RLiHK5Ehjw");
        builder.setEnabledIntents(EnumSet.allOf(GatewayIntent.class));

        builder.addEventListeners(new PinListener());
        builder.addEventListeners(new SettingsCommand());

        builder.setAutoReconnect(true);
        JDA jda = builder.build();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("pinsettings", "Change Pin Bot's settings.")
                        .addOption(OptionType.STRING, "webhook", "Set the webhook url.")
                        .addOption(OptionType.INTEGER, "amount", "Required amount of reactions.")
        );

        commands.queue();

    }

}
