package me.braggs.BraggBot.Commands.CommandFramework;

import me.braggs.BraggBot.CommandSender;
import net.dv8tion.jda.core.entities.*;

import java.util.List;

public class CommandEvent {
    private final CommandSender sender;
    private final Guild guild;
    private final MessageChannel channel;
    private final User user;
    private final Message message;
    private final List<String> arguments;

    public CommandEvent(CommandSender sender, Guild guild, MessageChannel channel, User user, Message message, List<String> arguments){
        this.guild = guild;
        this.channel = channel;
        this.user = user;
        this.message = message;
        this.arguments = arguments;
        this.sender = sender;
    }

    public Guild getGuild(){
        return guild;
    }

    public MessageChannel getChannel(){
        return channel;
    }

    public User getUser(){
        return user;
    }

    public Message getMessage() {
        return message;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getArgument(int index){
        return arguments.get(index);
    }

    public CommandSender getSender() {
        return sender;
    }
}
