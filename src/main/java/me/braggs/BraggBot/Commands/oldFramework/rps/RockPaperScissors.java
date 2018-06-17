package me.braggs.BraggBot.Commands.oldFramework.rps;
import me.braggs.BraggBot.*;
import me.braggs.BraggBot.Commands.oldFramework.CommandAnnotation;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.Method;
import java.util.*;

public class RockPaperScissors extends ListenerAdapter
{
    private int player1, player2;
    private Random rnd;

    public static RockPaperScissors instance;
    private RpsCommands rps;


    public RockPaperScissors()
    {
        instance = this;
        player1 = 0;
        player2 = 0;
        rnd = new Random();


        rps = new RpsCommands(this);
        Method[] methods = rps.getClass().getMethods();

        Reflector.addMethodsToManager(methods, rps, CommandAnnotation.commandType.rps, App.commandManager);
    }

    public void endSession()
    {
        Method[] methods = rps.getClass().getMethods();
        Reflector.removeMethodsFromManager(CommandAnnotation.commandType.rps, App.commandManager);
        instance = null;
    }

    public void addScore(boolean playerscored, CommandSender sender)
    {
        if(playerscored){player1++;}
        else{player2++;}
        checksession(sender);
    }

    private void checksession(CommandSender sender)
    {
        if(player1 >= 3)
        {
            sender.sendMessage("you win! thank's for playing");
            endSession();
        }
        else if(player2 >= 3)
        {
            sender.sendMessage("braggbot wins! better luck next time :/");
            endSession();
        }
    }
}
