package me.braggs.BraggBot.Listeners;

import me.braggs.BraggBot.App;
import me.braggs.BraggBot.Commands.oldFramework.Config;
import me.braggs.BraggBot.DiscordLogger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class JoinListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e)
    {
        User user = e.getUser();
        Guild joinedGuild = e.getGuild();
        MessageChannel channel = App.jdaBot.getTextChannelById(Config.getInstance().getAnnounceChannelID());

        if(Config.getInstance().getGuildID().equals(null) || Config.getInstance().getGuildID().equals("")){
            return;
        }

        if(!user.isBot() && joinedGuild.getId().equals(Config.getInstance().getGuildID()))
        {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setThumbnail(user.getAvatarUrl());
            embed.setColor(Color.GREEN);
            embed.setTitle(user.getName() + " has joined the guild! \uD83C\uDFEF");
            embed.setDescription("make sure to read through the rules. don't you go and cause trouble now! :rage:");
            channel.sendMessage(embed.build()).queue();
            DiscordLogger.logMessage("User " + user.getName() + " has joined the guild");
        }
    }
}
