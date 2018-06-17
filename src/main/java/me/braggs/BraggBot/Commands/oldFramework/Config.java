package me.braggs.BraggBot.Commands.oldFramework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.braggs.BraggBot.App;
import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.CommandArgs;
import me.braggs.BraggBot.Commandarguments.paramType;
import me.braggs.BraggBot.ResourceFinder;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Config {
    private static final Config instance;
    String botID = null;
    String superUserID = null;
    //1 = everyone, 2 = member, 3 = mods, 4 = admin, 5 = server owners
    Map<String, Integer> RoleLevels = null;
    String announceChannelID = null;
    String GuildID = null;

    String RedditUserName = null;
    String RedditPswd = null;
    String RedditID = null;
    String RedditSecret = null;

    boolean useChatterBot = false;
    String PandoraBotKey = null;
    String PandoraBotName = null;

    String MALuser = null;
    String MALpswd = null;

    String unSplashToken = null;
    String detectLanguageToken = null;

    String dbName = null;
    String dbUser = null;
    String dbPswd = null;

    String logChannelID = null;
    boolean lockDown = false;
    int VoiceVolume = 20;

    static {
        Config container = null;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(ResourceFinder.getResource("config.json")));
            Gson json = new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create();

            container = json.fromJson(reader, Config.class);
        }
        catch (FileNotFoundException e){
            System.out.println("CRITICAL ERROR, CONFIGFILE COULD NOT BE FOUND");
            System.exit(0);
        }
        instance = container;
    }

    public static Config getInstance(){
        return instance;
    }

    public String getLogChannelId() {
        return logChannelID;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPswd() {
        return dbPswd;
    }

    public String getUnSplashToken() {
        return unSplashToken;
    }

    public String getDetectLanguageToken() {
        return detectLanguageToken;
    }

    public String getRedditID() {
        return RedditID;
    }

    public String getRedditSecret() {
        return RedditSecret;
    }

    public String getBotID() {
        return botID;
    }

    public String getSuperUserID() {
        return superUserID;
    }

    public List<String> getRolesWithLevel(int level) {
        Set<String> roles = RoleLevels.keySet();
        List<String> rolesWithLevel = new ArrayList<String>();
        for (String role : roles) {
            if (RoleLevels.get(role) >= level) {
                rolesWithLevel.add(role);
            }
        }
        return rolesWithLevel;
    }

    public String getGuildID() {
        return GuildID;
    }

    public String getAnnounceChannelID() {
        return announceChannelID;
    }

    public String getRedditUserName() {
        return RedditUserName;
    }

    public String getRedditPswd() {
        return RedditPswd;
    }

    public boolean isPandoraBotEnabled(){
        return useChatterBot;
    }

    public String getPandoraBotKey() {
        return PandoraBotKey;
    }

    public String getPandoraBotName() {
        return PandoraBotName;
    }

    public String getMALuser() {
        return MALuser;
    }

    public String getMALpswd() {
        return MALpswd;
    }

    public int getVoiceVolume() {
        return VoiceVolume;
    }

    public void setVoiceVolume(int voiceVolume) {
        VoiceVolume = voiceVolume;
        writeToJson();
    }

    public boolean isLockDown() {
        return lockDown;
    }

    public void setLockDown(boolean lockDown) {
        this.lockDown = lockDown;
        writeToJson();
    }

    @CommandAnnotation(level = 99)
    public void getConfig(CommandSender sender, CommandArgs arguments){
        Field[] variables =  this.getClass().getDeclaredFields();
        System.out.println(variables.length);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(App.jdaBot.getSelfUser().getName() + "'s Config:");
        for(Field var : variables){
            try{
                builder.addField(var.getName(), "`" + var.get(this).toString() + "`", false);
            }
            catch (IllegalAccessException e){
                System.out.println(e);
            }
        }
        sender.sendMessage("understood my lord, it has been send to your DM's");
        sender.sendEmbedToDM(builder);
    }

    @CommandAnnotation(arguments = {paramType.StringCommand, paramType.String, paramType.String}, level = 99)
    public void setConfig(CommandSender sender, CommandArgs arguments){
        Field var;
        Object newValue;

        try{
            var = this.getClass().getDeclaredField(arguments.getArgument(1));
            System.out.println(var.getType());
        }
        catch (NoSuchFieldException e){
            sender.sendMessage("I dont have that variable in my current config :confused:");
            return;
        }
        if(var.getName().equals("RoleLevels")){
            sender.sendMessage("you cannot set this variable using this command, use `%setpermission` for this");
            return;
        }

        if(var.getType().equals(boolean.class)){
            newValue = BooleanUtils.toBoolean(arguments.getArgument(2));
            try {
                var.set(this, newValue);
            }
            catch (IllegalAccessException e){
                System.out.println(e);
            }
        }
        else if(var.getType().equals(int.class)){
            newValue = NumberUtils.toInt(arguments.getArgument(2));
            try{
                var.set(this, newValue);
            }
            catch (IllegalAccessException e){
                System.out.println(e);
            }
        }
        else {
            newValue = arguments.getArgument(2);
            try {
                var.set(this, newValue);
            }
            catch (IllegalAccessException e){
                System.out.println(e);
            }
        }
        writeToJson();
        sender.sendMessage("`" + var.getName() + "`" + " has been set to: " + newValue);
    }

    @CommandAnnotation(arguments = {paramType.StringCommand, paramType.String, paramType.Integer}, level = 99)
    public void setPermission(CommandSender sender, CommandArgs arguments){
        if(!RoleLevels.containsKey(arguments.getArgument(1))){
            sender.sendMessage("that role is not in my config :confused:");
            return;
        }
        if(Integer.parseInt(arguments.getArgument(2)) > 5){
            sender.sendMessage("sorry, but `5` is the limit");
            return;
        }
        if(Integer.parseInt(arguments.getArgument(2)) < 0){
            sender.sendMessage("what are you nuts?! you cant do that, `0` is the minimum!");
            return;
        }
        RoleLevels.replace(arguments.getArgument(1), Integer.parseInt(arguments.getArgument(2)));
        sender.sendMessage(arguments.getArgument(1) + " has been set to: " + arguments.getArgument(2));
        writeToJson();
    }

    private void writeToJson(){
        FileWriter fileWriter;
        Gson json = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        try {
            fileWriter = new FileWriter(ResourceFinder.getResource("config.json"));
            fileWriter.write(json.toJson(this));
            fileWriter.close();
        }
        catch (IOException e){
            System.out.println(e);
        }
    }
}
