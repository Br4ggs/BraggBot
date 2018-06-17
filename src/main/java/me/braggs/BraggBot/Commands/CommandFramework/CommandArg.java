package me.braggs.BraggBot.Commands.CommandFramework;

import me.braggs.BraggBot.Commandarguments.paramType;

public class CommandArg {
    public final paramType type;
    public final boolean optional;
    public final String defaultArgument;

    public CommandArg(paramType type, boolean optional, String defaultArgument){
        this.type = type;
        this.optional = optional;
        this.defaultArgument = defaultArgument;
    }

    public CommandArg(paramType type){
        this.type = type;
        this.optional = false;
        this.defaultArgument = "";
    }
}
