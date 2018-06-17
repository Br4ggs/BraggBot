package me.braggs.BraggBot.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.braggs.BraggBot.ResourceFinder;

import javax.swing.text.html.Option;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ConfigManager {

    public enum Singleton {
        INSTANCE;

        ConfigManager value;

        Singleton() {
            value = new ConfigManager();
        }

        public ConfigManager getInstance() {
            return value;
        }
    }

    public ConfigData getConfig() {
        return config;
    }

    private ConfigData config;

    private ConfigManager() {
        try {
            loadConfigFromJson();
        } catch (FileNotFoundException e) {
            System.out.println("CRITICAL ERROR, CONFIGDATA will be created");

            try {
                saveConfigToJson();
            } catch (IOException f) {
                System.out.println("CRITICAL ERROR, CONFIGDATA COULD NOT BE CREATED");
            }
            System.exit(0);
        }
        validateConfig();
    }

    //getters and setters for data here

    private void loadConfigFromJson() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(ResourceFinder.getResource("configdata.json")));
        Gson json = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        config = json.fromJson(reader, ConfigData.class);
    }

    private void saveConfigToJson() throws IOException {
        FileWriter fileWriter;
        Gson json = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        fileWriter = new FileWriter(ResourceFinder.getResource("configdata.json"));
        fileWriter.write(json.toJson(new ConfigData()));
        fileWriter.close();
    }

    private void validateConfig() {
        System.out.println("validating config");

        for (Field field : config.getClass().getDeclaredFields()) {
            if (OptionalData.class.isAssignableFrom(field.getType())) {
                try{
                    OptionalData data = (OptionalData) field.get(config);
                    //System.out.println(data);

                    if (data.useData){
                        for (Field subField : field.getType().getDeclaredFields()) {
                            String param = (String) subField.get(data);
                            if(param.toLowerCase().contains("insert") || param.isEmpty()){
                                System.out.println("ERROR REQUIRED FIELD " + subField.getName() + " IN " + field.getName() + " IS EMPTY OR STILL HAS DEFAULT ARGUMENT");
                                System.exit(1);
                            }
                        }
                    }
                }
                catch (IllegalAccessException e){
                    System.out.println(e);
                }
            }
        }
    }
}
