package me.braggs.BraggBot.Commands.oldFramework;

import com.google.gson.*;
import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commandarguments;
import me.braggs.BraggBot.Commands.CommandArgs;
import me.braggs.BraggBot.Commands.Languages;
import net.dv8tion.jda.core.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Base64;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;


public class FetchCommands {
    int xkcdNums = 1931;
    Random rand = new Random();
    OkHttpClient client;
    public Languages languages;

    public FetchCommands(){
        client = new OkHttpClient();
        languages = new Languages();

        Request request = new Request.Builder()
                .url("http://xkcd.com/info.0.json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObj = builder.fromJson(response.body().string(), JsonObject.class);

            xkcdNums = Integer.parseInt(jsonObj.get("num").getAsString());
        }
        catch (IOException e){
            System.out.println("ERROR: " + e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Sentence}, typeOfCommand = CommandAnnotation.commandType.fetch, level = 1)
    public void image(CommandSender sender, CommandArgs arguments){
        Request request = new Request.Builder()
                .url("https://api.unsplash.com/photos/random?count=1&query=" + arguments.getArgument(1).replace(" ", "-"))
                .addHeader("Authorization", "Client-ID " + Config.getInstance().getUnSplashToken())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(!response.isSuccessful()){
                sender.sendMessage("I wasn't able to find anything for that searchterm :sob:");
                return;
            }
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonArray jsonArray = builder.fromJson(response.body().string(), JsonArray.class);

            for(int i=0; i < jsonArray.size(); i++){
                String author = jsonArray.get(i).getAsJsonObject().get("user").getAsJsonObject().get("name").getAsString();
                String profile = jsonArray.get(i).getAsJsonObject().get("user").getAsJsonObject().get("links").getAsJsonObject().get("html").getAsString();
                String iconURL = jsonArray.get(i).getAsJsonObject().get("user").getAsJsonObject().get("profile_image").getAsJsonObject().get("small").getAsString();
                String unsplashLink = jsonArray.get(i).getAsJsonObject().get("links").getAsJsonObject().get("html").getAsString();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Result for: " + arguments.getArgument(1), unsplashLink)
                        .setAuthor("Author: " + author , profile, iconURL)
                        .setColor(hex2Rgb(jsonArray.get(0).getAsJsonObject().get("color").getAsString()))
                        .setImage(jsonArray.get(i).getAsJsonObject().get("urls").getAsJsonObject().get("full").getAsString())
                        .setFooter("image provided by Unsplash", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Logo_of_Unsplash.svg/2000px-Logo_of_Unsplash.svg.png");
                sender.sendEmbed(embed);
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Sentence}, typeOfCommand = CommandAnnotation.commandType.fetch, level = 1)
    public void anime(CommandSender sender, CommandArgs arguments) {
        String parsedQuerry = arguments.getArgument(1).replaceAll(" ", "+");
        String authHeader = Config.getInstance().getMALuser() + ":" + Config.getInstance().getMALpswd();
        byte[] encodedBytes = Base64.getEncoder().encode(authHeader.getBytes());
        Request request = new Request.Builder()
                .url("https://myanimelist.net/api/anime/search.xml?q=" + parsedQuerry)
                .addHeader("Accept", "text/xml, text/*")
                .addHeader("Accept-Charset","utf-8")
                .addHeader("Authorization","Basic " + new String(encodedBytes))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            if(responseString.isEmpty()){
                sender.sendMessage("sorry no animu found with that name :japanese_goblin:");
                return;
            }
            File input = new File(responseString);
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new InputSource(new StringReader(responseString)));
            List<Element> elements = document.getRootElement().getChildren();
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(":book: " + elements.get(0).getChild("title").getValue())
                    .setDescription("episodes: " + elements.get(0).getChild("episodes").getValue())
                    .addField("score", elements.get(0).getChild("score").getValue(), true)
                    .addField("status", elements.get(0).getChild("status").getValue(), true)
                    .addField("synopsis", sanitize(elements.get(0).getChild("synopsis").getValue()) + "\n**[For full synopsis visit MyAnimeList page]**", false)
                    .setImage(elements.get(0).getChild("image").getValue())
                    .setColor(Color.blue);
            sender.sendEmbed(embed);

        }
        catch (IOException | JDOMException e){
            System.out.println("ERROR: " + e);
            return;
        }
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.fetch, level = 1)
    public void xkcd(CommandSender sender, CommandArgs arguments) {
        int rndNum = rand.nextInt(xkcdNums) + 1;
        Request request = new Request.Builder()
                .url("http://xkcd.com/" + rndNum + "/info.0.json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObj = builder.fromJson(response.body().string(), JsonObject.class);

            System.out.println(xkcdNums);
            String link = "https://xkcd.com/" + rndNum;
            String img = jsonObj.get("img").getAsString();
            String title = jsonObj.get("safe_title").getAsString();
            String alt = jsonObj.get("alt").getAsString();
            EmbedBuilder embed = new EmbedBuilder().setTitle(title, link)
                    .setDescription(alt)
                    .setImage(img);
            sender.sendEmbed(embed);
        }
        catch (IOException e){
            System.out.println("ERROR: " + e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.TranslationLang, Commandarguments.paramType.Sentence}, typeOfCommand = CommandAnnotation.commandType.fetch, level = 1)
    public void translate(CommandSender sender, CommandArgs arguments){
        String translateToCode = languages.getLanguage(arguments.getArgument(1));
        Gson builder = new GsonBuilder().setPrettyPrinting().create();

        Request detectLangRequest = new Request.Builder()
                .url("http://ws.detectlanguage.com/0.2/detect?q=" + arguments.getArgument(2).replaceAll(" ", "+") +"&key=" + Config.getInstance().getDetectLanguageToken())
                .build();

        Response detectResponse, translatedResponse;
        String lang = "", confidence = "";
        JsonArray translatedJson = new JsonArray();
        try{
            detectResponse = makeRequest(detectLangRequest);
            JsonObject detectJson = builder.fromJson(detectResponse.body().string(), JsonObject.class);

            lang = detectJson.get("data").getAsJsonObject().get("detections").getAsJsonArray().get(0).getAsJsonObject().get("language").getAsString();
            confidence = detectJson.get("data").getAsJsonObject().get("detections").getAsJsonArray().get(0).getAsJsonObject().get("confidence").toString();

            Request translateRequest = new Request.Builder()
                    .url("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + lang + "&tl=" + translateToCode + "&dt=t&q=" + arguments.getArgument(2).replaceAll(" ", "+"))
                    .build();

            translatedResponse = makeRequest(translateRequest);
            translatedJson = builder.fromJson(translatedResponse.body().string(), JsonArray.class);
        }
        catch (IOException | NullPointerException e){
            System.out.println(e);
        }
        StringBuilder sanitized = new StringBuilder();
        translatedJson.get(0).getAsJsonArray()
                .forEach(jsonElement -> sanitized.append(jsonElement.getAsJsonArray().get(0).getAsString()));

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Sentence translated from: " + languages.findKeyByValue(lang))
                .setDescription(sanitized)
                .addField("confidence", confidence, true);

        sender.sendEmbed(embed);
    }

    @CommandAnnotation(typeOfCommand = CommandAnnotation.commandType.fetch, level = 1)
    public void viewLangs(CommandSender sender, CommandArgs arguments){
        StringBuilder langs = new StringBuilder();
        languages.languageMap.forEach((s, s2) -> langs.append(" " + s + ","));
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Languages available for translation")
                .setDescription(langs);
        sender.sendEmbed(embed);
    }

    private String sanitize(String input){
        String filteredString = input.replaceAll("<br />", "\n")
                .replaceAll("&#039;", "'")
                .replaceAll("&mdash;", "-")
                .replaceAll("&quot;", "_");
        StringBuilder guttedStringBuilder = new StringBuilder();
        for(char character : filteredString.toCharArray()){
            if(guttedStringBuilder.length() > 800){
                guttedStringBuilder.append("...");
                return guttedStringBuilder.toString();
            }
            guttedStringBuilder.append(character);
        }
        return guttedStringBuilder.toString();
    }

    Response makeRequest(Request request){
        try{
            return client.newCall(request).execute();
        }
        catch (IOException e){
            System.out.println(e);
            return null;
        }
    }

    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }
}
