package me.braggs.BraggBot;

import me.braggs.BraggBot.Commands.oldFramework.Config;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscordLogger {
    private static final MessageChannel logChannel;

    static {
        logChannel = App.jdaBot.getTextChannelById(Config.getInstance().getLogChannelId());
    }

    public static void logMessage(String msg){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String message = "**[" + dateFormat.format(date) + "]**" + " " + msg;
        logChannel.sendMessage(message).queue();
    }
}
