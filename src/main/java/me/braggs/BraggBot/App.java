package me.braggs.BraggBot;//Made by Braggs with love <3

import me.braggs.BraggBot.Commands.CommandManager;
import me.braggs.BraggBot.Commands.oldFramework.*;
import me.braggs.BraggBot.Configuration.ConfigManager;
import me.braggs.BraggBot.Listeners.CommandListener;
import me.braggs.BraggBot.Listeners.JoinListener;
import me.braggs.BraggBot.Listeners.MentionListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class App
{
    public static JDA jdaBot;

    public static RedditCommands redditCommands;
    public static FunCommands fun;
    public static VoiceCommands voiceCommands;
    public static FetchCommands fetchCommands;
    public static CommandManager commandManager;
    public static ModCommands modCommands;

    public static void main(String[] args) throws Exception {
        jdaBot = new JDABuilder(AccountType.BOT).setToken(Config.getInstance().getBotID()).buildBlocking();

        jdaBot.addEventListener(new MentionListener());
        jdaBot.addEventListener(new JoinListener());
        jdaBot.addEventListener(new CommandListener(CommandManager.Singleton.INSTANCE.getInstance()));
        jdaBot.getPresence().setGame(Game.of("%listcommands"));

        DiscordLogger.logMessage("Braggbot is online!");

        ConfigManager.Singleton.INSTANCE.getInstance();
    }
}