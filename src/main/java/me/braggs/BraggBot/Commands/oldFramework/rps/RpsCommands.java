package me.braggs.BraggBot.Commands.oldFramework.rps;

import me.braggs.BraggBot.Commands.oldFramework.CommandAnnotation;
import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.*;
import me.braggs.BraggBot.Commandarguments.paramType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RpsCommands
{
    private Map<String, Integer> commandMap = new HashMap<>();
    private RockPaperScissors rpsInstance;
    private Random rnd;

    public RpsCommands(RockPaperScissors rpsInstance)
    {
        commandMap.put("rock", 0);
        commandMap.put("scissor", 1);
        commandMap.put("paper", 2);
        this.rpsInstance = rpsInstance;
        rnd = new Random();
    }

    @CommandAnnotation(arguments = {paramType.StringCommand, paramType.String}, typeOfCommand = CommandAnnotation.commandType.rps, level = 1)
    public void rpsplay(CommandSender sender, CommandArgs arguments)
    {
        int playerChoice;
        int botChoice;
        String botString;
        if(arguments.getSize() == 2)
        {
            if(commandMap.get(arguments.getArgument(1)) != null)
            {
                playerChoice = commandMap.get(arguments.getArgument(1));
                botChoice = rnd.nextInt(3);
                switch (botChoice)
                {
                    case 0:
                        botString = "braggbot chose rock, ";
                        break;
                    case 1:
                        botString = "braggbot chose scissors, ";
                        break;
                    case 2:
                        botString = "braggbot chose paper, ";
                        break;
                    default:
                        botString = "error";
                }
                switch (comparator(playerChoice,botChoice))
                {
                    case 0:
                        sender.sendMessage(botString + "it's a tie!");
                        break;
                    case 1:
                        sender.sendMessage(botString + "you win!");
                        rpsInstance.addScore(true, sender);
                        break;
                    case 2:
                        sender.sendMessage(botString + "braggbot wins! :0");
                        rpsInstance.addScore(false, sender);
                        break;
                }
            }
            else {
                sender.sendMessage("specify either `rock, paper or scissor` please");
            }
        }
    }

    private int comparator(int p1, int p2)
    {
        if(p1 == (p2 - 1) || (p1 % 2) == 0 && p2 == 0){return 1;}
        else if(p1 == p2){return 0;}
        else {return 2;}
    }
}
