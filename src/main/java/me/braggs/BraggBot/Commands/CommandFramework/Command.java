package me.braggs.BraggBot.Commands.CommandFramework;

import java.util.function.Consumer;

public class Command{
    String name;
    CommandCategory category;
    CommandArg[] parameters;
    Consumer<CommandEvent> method;

    public Command setName(String name) {
        this.name = name;
        return this;
    }

    public Command setCategory(CommandCategory category) {
        this.category = category;
        return this;
    }

    public Command setParameters(CommandArg... arguments) {
        this.parameters = arguments;
        return this;
    }

    public Command setMethod(Consumer<CommandEvent> method) {
        this.method = method;
        return this;
    }

    public void execute(CommandEvent event) {
        method.accept(event);
    }

    public String getName() {
        return name;
    }

    public CommandCategory getCategory() {
        return category;
    }

    public CommandArg[] getParameters() {
        return parameters;
    }
}
