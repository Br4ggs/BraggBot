package me.braggs.BraggBot.Commands;

import java.util.List;

public class CommandArgs
{
    private final List<String> args;

    public CommandArgs(List<String> args){
        this.args = args;
    }

    public void addArgument(String arg)
    {
        args.add(arg);
    }
    public String getArgument(int index)
    {
        return args.get(index);
    }
    public int getSize()
    {
        return args.size();
    }
    public List<String> getArgumentList()
    {
        return args;
    }
}
