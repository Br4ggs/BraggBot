package me.braggs.BraggBot.Commands.oldFramework;

import me.braggs.BraggBot.*;
import me.braggs.BraggBot.Commands.oldFramework.CommandAnnotation.commandType;
import me.braggs.BraggBot.Commands.CommandArgs;
import me.braggs.BraggBot.Commands.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PermissionOverride;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModCommands {

    @CommandAnnotation(typeOfCommand = commandType.mod, level = 3)
    public void viewIgnores(CommandSender sender, CommandArgs arguments){

        try{
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("SELECT * FROM ignored_users");
            ResultSet rs = ps.executeQuery();
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("users ignored by braggbot")
                    .addField("ignore level(up to)", "`" + CommandManager.getIgnoreLevel() + "`", false);
            StringBuilder builder = new StringBuilder();
            while (rs.next()){
                long id = rs.getLong("user_id");
                Date date = rs.getDate("date");
                builder.append("**" + findUserNameById(id) + "**\t" + Long.toString(id) + "\t| " + date.toString() + "\n");
            }


            embed.setDescription(builder);
            sender.sendEmbed(embed);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String findUserNameById(long id){
        try{
            return App.jdaBot.getUserById(id).getName();
        }
        catch (NullPointerException e){
            return "UNKOWN USER";
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Long}, typeOfCommand = commandType.mod, level = 3)
    public void ignore(CommandSender sender, CommandArgs arguments){
        if(CommandManager.getIgnoredUsers().contains(Long.parseLong(arguments.getArgument(1)))){
            sender.sendMessage("im already ignoring that user");
            return;
        }

        try {
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("INSERT INTO ignored_users (user_id) VALUES (?);");
            ps.setString(1, arguments.getArgument(1));
            ps.executeUpdate();

            CommandManager.addIgnoredUser(Long.parseLong(arguments.getArgument(1)));
            sender.sendMessage("la la la, i cant hear you " + findUserNameById((Long.parseLong(arguments.getArgument(1)))));
        }
        catch (SQLException e){
            System.out.println(e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Long}, typeOfCommand = commandType.mod, level = 3)
    public void unignore(CommandSender sender, CommandArgs arguments){
        if(!CommandManager.getIgnoredUsers().contains(Long.parseLong(arguments.getArgument(1)))){
            sender.sendMessage("i wasn't even ignoring that user");
            return;
        }
        try{
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("DELETE FROM ignored_users WHERE user_id = ?;");
            ps.setString(1, arguments.getArgument(1));
            ps.executeUpdate();

            CommandManager.removeIgnoredUser(Long.parseLong(arguments.getArgument(1)));
            sender.sendMessage("ah i can hear " + findUserNameById((Long.parseLong(arguments.getArgument(1)))) + " again :^)");
        }
        catch (SQLException e){
            System.out.println(e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Integer}, typeOfCommand = commandType.mod, level = 5)
    public void setIgnoreLevel(CommandSender sender, CommandArgs arguments){
        int ignorelvl = Integer.parseInt(arguments.getArgument(1));
        if(ignorelvl > 5){ ignorelvl = 5; }
        if(ignorelvl < 0){ ignorelvl = 0; }
        CommandManager.setIgnoreLevel(ignorelvl);
        sender.sendMessage("ill now ignore users up to level `" + ignorelvl + "`");
    }

    //@CommandAnnotation(arguments = {paramType.StringCommand,paramType.Number})
    public void lockChat(CommandSender sender, CommandArgs arguments){
        List<PermissionOverride> permissions = sender.guild.getTextChannelById(arguments.getArgument(1)).getManager().getChannel().getRolePermissionOverrides();
        System.out.println(permissions.size());
        System.out.println(permissions.get(1).getRole());
        permissions.get(1).getManagerUpdatable().deny(Permission.ALL_PERMISSIONS).update().queue();
    }

    @CommandAnnotation(typeOfCommand = commandType.mod, level = 99)
    public void shutdown(CommandSender sender, CommandArgs arguments) {
        sender.sendMessageComplete("Understood, shutting down...");
        DiscordLogger.logMessage("shutting down braggbot...");
        App.jdaBot.shutdownNow();
        System.exit(0);
    }

    @CommandAnnotation(typeOfCommand = commandType.mod, level = 99)
    public void restart(CommandSender sender, CommandArgs arguments) {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        try {
            final File currentJar = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            /* is it a jar file? */
            if (!currentJar.getName().endsWith(".jar")){
                System.out.println("not a jar");
                return;
            }

            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            sender.sendMessage("brb, restarting...");
            DiscordLogger.logMessage("restarting braggbot...");
            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            App.jdaBot.shutdownNow();
            System.exit(0);

        } catch (IOException | URISyntaxException e) {
            System.out.println(e);
        }
    }
}
