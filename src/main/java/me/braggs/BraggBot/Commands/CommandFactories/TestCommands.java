package me.braggs.BraggBot.Commands.CommandFactories;

import me.braggs.BraggBot.Commandarguments;
import me.braggs.BraggBot.Commands.CommandFramework.*;

import java.util.ArrayList;
import java.util.List;

@CommandFactory
public class TestCommands implements CommandBuilder {
    private CommandCategory commandCategory = CommandCategory.test;

    @Override
    public List<Command> buildCommandList() {
        List<Command> commandList = new ArrayList<>();

        Command testCommand = new Command()
                .setName("testCommandFramework")
                .setCategory(commandCategory)
                .setMethod(event -> System.out.println("this is a test"));
        commandList.add(testCommand);


        Command anotherTestCommand = new Command()
                .setName("anotherCommandFrameworkTest")
                .setCategory(commandCategory)
                .setParameters(new CommandArg(Commandarguments.paramType.Sentence), new CommandArg(Commandarguments.paramType.String))
                .setMethod(event -> {
                    event.getSender().sendMessage("this was send using the new command framework: " + event.getArgument(0) + " and: " + event.getArgument(1));
                });
        commandList.add(anotherTestCommand);

        return commandList;
    }
}
