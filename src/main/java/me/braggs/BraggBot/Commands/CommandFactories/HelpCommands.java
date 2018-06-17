package me.braggs.BraggBot.Commands.CommandFactories;

import com.google.gson.*;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;
import me.braggs.BraggBot.Commands.CommandFramework.*;
import me.braggs.BraggBot.Commands.CommandManager;
import me.braggs.BraggBot.Commandarguments.paramType;
import me.braggs.BraggBot.ResourceFinder;
import net.dv8tion.jda.core.EmbedBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CommandFactory
public class HelpCommands implements CommandBuilder {
    CommandCategory commandCategory = CommandCategory.help;

    @Override
    public List<Command> buildCommandList() {
        List<Command> commandList = new ArrayList<>();

        Command listCommands = new Command()
                .setName("listCommands")
                .setCategory(commandCategory)
                .setMethod(event -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setDescription("There are: " + CommandCategory.values().length + " command categories");

                    List<Command> commands = new ArrayList<>(CommandManager.Singleton.INSTANCE.getInstance().getCommands().values());

                    for (CommandCategory category : CommandCategory.values()){
                        List<Command> filteredList = commands.stream()
                                .filter(command -> command.getCategory().equals(category))
                                .collect(Collectors.toList());

                        String commandlist = String.join(", ", filteredList.stream().map(command -> command.getName()).collect(Collectors.toList()));

                        if(filteredList.size() > 0){
                            embed.addField(category.name(), "```" + commandlist + "```", false);
                        }
                    }

                    event.getSender().sendEmbed(embed);
                });
        commandList.add(listCommands);

        Command commandInfo = new Command()
                .setName("help")
                .setCategory(commandCategory)
                .setParameters(new CommandArg(paramType.String, true, "menu"))
                .setMethod(event -> {
                    try {
                        //for later if validation is fixed
                        if(event.getArgument(0).equals("menu")){
                            event.getSender().sendMessage("display help menu");
                            return;
                        }

                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(new FileReader(ResourceFinder.getFile("commandHelp.json"))).getAsJsonObject();

                        for (CommandCategory category : commandCategory.values()) {
                            if (category.name().equals(event.getArgument(0))){
                                JsonElement description = json.get("categoryDescriptions").getAsJsonObject().get(event.getArgument(0));
                                if(description == null){
                                    event.getSender().sendMessage("Undocumented category description");
                                    return;
                                }
                                else {
                                    event.getSender().sendMessage("**" + category.name() + "**: " + description.getAsString());
                                    return;
                                }
                            }
                        }

                        Set<String> commands = CommandManager.Singleton.INSTANCE.getInstance().getCommands().keySet().stream()
                                .map(s -> s.replace(CommandManager.prefix, ""))
                                .collect(Collectors.toSet());

                        for (String command : commands){
                            if(command.equals(event.getArgument(0).toLowerCase())){
                                JsonArray descriptions = json.get("commandDescriptions").getAsJsonArray();

                                for(JsonElement element : descriptions){
                                    JsonObject object = element.getAsJsonObject();

                                    if(object.get("name").getAsString().toLowerCase().equals(event.getArgument(0).toLowerCase())){
                                        EmbedBuilder embed = new EmbedBuilder();
                                        embed.setTitle(object.get("name").getAsString());
                                        embed.setDescription(object.get("description").getAsString());
                                        embed.addField("category", object.get("category").getAsString(), true);
                                        embed.addField("structure", "`" + object.get("structure").getAsString() + "`", true);
                                        embed.addField("example", "`" + object.get("example").getAsString().replace("<PREFIX>", CommandManager.prefix) + "`", false);

                                        event.getSender().sendEmbed(embed);
                                        return;
                                    }
                                }

                                event.getSender().sendMessage("Undocumented command");
                                return;
                            }
                        }

                        event.getSender().sendMessage("Nothing i know corresponds with that term, try again");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                });
        commandList.add(commandInfo);

        return commandList;
    }
}
