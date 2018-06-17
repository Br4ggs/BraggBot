package me.braggs.BraggBot.Listeners;

import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CommandListener extends ListenerAdapter
{
    CommandManager commandManager;

    public CommandListener(CommandManager manager) {
        commandManager = manager;
        System.out.println("constructor called");
        System.out.println(commandManager);
    }

    @Override //entry point for users issuing commands
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        Message objMsg = e.getMessage();
        MessageChannel objChannel = e.getChannel();
        User objUser = e.getAuthor();
        Guild objGuild = e.getGuild();
        List<String> arguments = new ArrayList<>();

        //load the argument list with commands!!!
        Scanner argScanner = new Scanner(objMsg.getRawContent());
        while(argScanner.hasNext()) {
            arguments.add(argScanner.next());
        }

        if(!objUser.isBot()) {
            if(!objMsg.getContent().equals("")) {
                commandManager.runMethod(new CommandSender(objChannel, objMsg, objUser, objGuild), arguments);
            }
        }
    }
}
