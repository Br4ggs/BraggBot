package me.braggs.BraggBot.Commands;

import me.braggs.BraggBot.ResourceFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Languages {
    public HashMap<String, String> languageMap = new HashMap<>();
    public Languages(){
        List<String> resource = new ArrayList<>();
        try{
            resource = Files.readAllLines(Paths.get(ResourceFinder.getResource("translateLangs.txt")));
        }
        catch (IOException e){
            System.out.println(e);
        }
        for (String line : resource){
            String[] langCodes = line.split(" ");
            languageMap.put(langCodes[0],langCodes[1]);
        }
    }

    public String getLanguage(String lang){
        return languageMap.get(lang);
    }

    public String findKeyByValue(String langcode){
        return languageMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(langcode))
                .map(entry -> entry.getKey())
                .findFirst()
                .orElse("???");
    }

}
