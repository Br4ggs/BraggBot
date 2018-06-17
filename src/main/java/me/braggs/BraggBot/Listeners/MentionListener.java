package me.braggs.BraggBot.Listeners;

import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.CommandManager;
import me.braggs.BraggBot.Commands.oldFramework.Config;
import me.braggs.BraggBot.Validator;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import com.google.code.chatterbotapi.*;

import java.util.regex.Pattern;


public class MentionListener extends ListenerAdapter
{
    ChatterBotFactory factory;
    ChatterBot chatterBot;
    ChatterBotSession session;
    public MentionListener(){
        if(Config.getInstance().isPandoraBotEnabled()){
            factory = new ChatterBotFactory();
            try{
                chatterBot = factory.create(ChatterBotType.PANDORABOTS, Config.getInstance().getPandoraBotKey());
            }
            catch (Exception e){
                System.out.println(e);
            }
            session = chatterBot.createSession();
        }
    }
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        JDA bot = e.getJDA();
        Message message = e.getMessage();
        MessageChannel channel = e.getChannel();
        User user = e.getAuthor();
        Guild guild = e.getGuild();
        //checks if Braggbot was mentioned
        if(!e.getAuthor().isBot())
        {
            if(CommandManager.getIgnoredUsers().contains(user.getIdLong()) && Validator.checkLevel(guild, user.getIdLong(), CommandManager.getIgnoreLevel()) != 1){ return; }
            if(message.getRawContent().toLowerCase().contains(bot.getSelfUser().getName().toLowerCase()) || message.isMentioned(bot.getSelfUser()))
            {
                message.addReaction("\uD83C\uDDEA").queue();
                message.addReaction("\uD83C\uDDFE").queue();
                if(Config.getInstance().isPandoraBotEnabled() && !Config.getInstance().isLockDown()){
                    String feed = message.getContent().toLowerCase().replaceAll("[^A-Za-z0-9 ]|" + bot.getSelfUser().getName().toLowerCase() + " ", "");
                    System.out.println(feed);
                    if(feed.isEmpty()){ return; }

                    channel.sendTyping().queue();
                    CommandSender sender = new CommandSender(channel,message,user,null);
                    try{
                        String response = session.think(feed).replaceAll(Config.getInstance().getPandoraBotName(), bot.getSelfUser().getName())
                                        .replaceAll("<br>", "\n")
                                        .replaceAll(Pattern.CASE_INSENSITIVE + Config.getInstance().getPandoraBotName(), bot.getSelfUser().getName())
                                        .replaceAll("ALICE AI Foundation|A\\.L\\.I\\.C\\.E\\. AI Foundation|ALICE", "Braggs")
                                        .replaceAll("Pennsylvania", "Eindhoven");

                        if(response.isEmpty()){
                            response = "i have no idea what you just said";
                        }
                        sender.sendMessage(response);
                    }
                    catch (Exception b){
                        System.out.println(b);
                    }
                }
            }
        }
    }
}
