package me.braggs.BraggBot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.InputStream;

public class CommandSender {
    public MessageChannel messageChannel;
    public Message message;
    public User user;
    public Guild guild;

    public CommandSender(MessageChannel msgchannel, Message msg, User usr, Guild gld) {
        messageChannel = msgchannel;
        message = msg;
        user = usr;
        guild = gld;
    }

    public void sendEmbedToDM(EmbedBuilder embed) {
        PrivateChannel userChannel = user.openPrivateChannel().complete();
        userChannel.sendMessage(embed.build()).queue();
    }

    public void sendMessageToDM(String message) {
        PrivateChannel userChannel = user.openPrivateChannel().complete();
        userChannel.sendMessage(message).queue();
    }

    public void sendMessage(String message) {
        messageChannel.sendMessage(message).queue();
    }

    public void sendMessageComplete(String message) {
        messageChannel.sendMessage(message).complete();
    }

    public void sendEmbed(EmbedBuilder embed) {
        messageChannel.sendMessage(embed.build()).queue();
    }

    public void sendFile(File file, String message) {
        Message msg = new MessageBuilder().append(message).build();
        messageChannel.sendFile(file, msg).queue();
    }

    public void sendByteArray(byte[] bytes, String fileName, String message) {
        Message msg = new MessageBuilder().append(message).build();
        messageChannel.sendFile(bytes, fileName, msg).queue();
    }
}
