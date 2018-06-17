package me.braggs.BraggBot.Configuration;

import com.google.code.chatterbotapi.ChatterBotType;

import java.util.HashMap;
import java.util.Map;

public class ConfigData {
    ///////////////////////
    //REQUIRED PARAMETERS//
    ///////////////////////

    //main ID
    public String botID = "insert bot ID here";
    public String superUserID = "insert super user ID here";
    public Map<String, Integer> RoleLevels = new HashMap<>();

    //discord
    public String guildID = "insert guild ID here";
    public String anouncechannelID = "insert anounce channel ID here";
    public String logchannelID = "insert log channel ID here";

    //database
    public String databaseName = "insert db name here";
    public String databaseUser = "insert db username here";
    public String databasePassword = "insert db username password here";

    ///////////////////////
    //OPTIONAL PARAMETERS//
    ///////////////////////

    //reddit
    public RedditData redditData = new RedditData();

    //myAnimeList
    public MALData myanimelistData = new MALData();

    //chatterbot
    public ChatterbotData chatterbotData = new ChatterbotData();

    //unsplash
    public UnsplashData unsplashData = new UnsplashData();

    //translation
    public TranslationData translationData = new TranslationData();
}

class RedditData extends OptionalData {
    public String redditID = "insert Reddit ID here";
    public String redditSecret = "insert reddit secret here";
    public String redditUserName = "insert Reddit username here";
    public String redditPassword = "insert reddit user password here";
}

class MALData extends OptionalData {
    public String MALuser = "insert MyAnimeList username here";
    public String MALpassword = "insert MyAnimeList user password here";
}

class ChatterbotData extends OptionalData {
    public String chatterBotType = "insert chatterbot type here";
    public String chatterBotKey = "insert chatterbot key here";
}

class UnsplashData extends OptionalData {
    public String unsplashToken = "insert unsplash token here";
}

class TranslationData extends OptionalData {
    public String detectLanguageToken = "insert token here";
}

//base class to signify a set of data which can be used depending on the "useData" value
abstract class OptionalData {
    public boolean useData = false;
}
