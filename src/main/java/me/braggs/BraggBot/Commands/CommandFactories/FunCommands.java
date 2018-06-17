package me.braggs.BraggBot.Commands.CommandFactories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.braggs.BraggBot.Commandarguments.paramType;
import me.braggs.BraggBot.Commands.CommandFramework.Command;
import me.braggs.BraggBot.Commands.CommandFramework.CommandArg;
import me.braggs.BraggBot.Commands.CommandFramework.CommandBuilder;
import me.braggs.BraggBot.Commands.CommandFramework.CommandCategory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FunCommands implements CommandBuilder {
    private CommandCategory commandCategory = CommandCategory.fun;

    Random rnd = new Random();
    OkHttpClient client = new OkHttpClient();

    @Override
    public List<Command> buildCommandList() {
        List<Command> commandList = new ArrayList<>();

        Command ping = new Command()
                .setName("ping")
                .setCategory(commandCategory)
                .setMethod(event -> event.getSender().sendMessage("pong"));
        commandList.add(ping);

        Command rtd = new Command()
                .setName("rtd")
                .setCategory(commandCategory)
                .setParameters(new CommandArg(paramType.Integer, true, "6"))
                .setMethod(event -> {
                    int sides = Integer.parseInt(event.getArgument(0));
                    int result = rnd.nextInt(sides) + 1;
                    event.getSender().sendMessage("you rolled a die with `" + sides + "` sides, you rolled a: " + result);
                });
        commandList.add(rtd);

        Command joke = new Command()
                .setName("joke")
                .setCategory(commandCategory)
                .setMethod(event -> {
                    try {
                        Request request = new Request.Builder()
                                .url("https://08ad1pao69.execute-api.us-east-1.amazonaws.com/dev/random_joke")
                                .build();

                        Response response = client.newCall(request).execute();
                        Gson builder = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject jsonObj = builder.fromJson(response.body().string(), JsonObject.class);

                        event.getSender().sendMessage(jsonObj.get("setup").getAsString() + "\n" + jsonObj.get("punchline").getAsString());
                    } catch (IOException e) {
                        event.getSender().sendMessage("I'm afraid there won't be any jokes today, sad!");
                        //log error with discord logger
                    }
                });
        commandList.add(joke);

        Command jpeg = new Command()
                .setName("jpeg")
                .setCategory(commandCategory)
                .setParameters(new CommandArg(paramType.Url))
                .setMethod(event -> {
                    try {
                        URL url = new URL(event.getArgument(0));
                        BufferedImage img = ImageIO.read(url);

                        JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
                        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        jpegParams.setCompressionQuality(0.01f);

                        ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
                        ImageOutputStream imgOs = ImageIO.createImageOutputStream(byteOs);

                        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
                        writer.setOutput(imgOs);

                        writer.write(null, new IIOImage(img, null, null), jpegParams);

                        byte[] data = byteOs.toByteArray();

                        event.getSender().sendByteArray(data, "Jpeg.jpeg", "Added moar Jpeg!");
                    } catch (IOException e) {
                        event.getSender().sendMessage("Woopsie I can't Jpeggify that image");
                    }
                });
        commandList.add(jpeg);

        return commandList;
    }
}
