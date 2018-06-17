package me.braggs.BraggBot.Commands;

import javafx.util.Pair;
import me.braggs.BraggBot.App;
import me.braggs.BraggBot.CommandSender;
import me.braggs.BraggBot.Commands.CommandFactories.FunCommands;
import me.braggs.BraggBot.Commands.CommandFactories.HelpCommands;
import me.braggs.BraggBot.Commands.CommandFactories.RedditCommands;
import me.braggs.BraggBot.Commands.CommandFactories.TestCommands;
import me.braggs.BraggBot.Commands.CommandFramework.CommandEvent;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import me.braggs.BraggBot.Commands.CommandFramework.Command;
import me.braggs.BraggBot.DatabaseManager;
import me.braggs.BraggBot.Validator;

public class CommandManager
{
    public enum Singleton {
        INSTANCE;

        CommandManager value;

        Singleton(){
            value = new CommandManager();
        }

        public CommandManager getInstance(){
            return value;
        }
    }

    static public String prefix = "%";

    private Thread rtfmTimer;
    private int timerCount = 0;
    private boolean timerToggle = false;
    private static int ignoreLevel = 3;

    private static List<Long> ignoredUsers = new ArrayList<>();
    private static Map<String, String> macros = new HashMap<>();
    public static Map<String, Pair<Method,Object>> methods = new HashMap<>();

    private Map<String, Command> commands;

    private CommandManager() {
        try{
            ResultSet ignoredUserSet = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("SELECT * FROM ignored_users")
                    .executeQuery();

            ResultSet macroSet = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("SELECT * FROM macros")
                    .executeQuery();

            while (ignoredUserSet.next()){
                System.out.println(ignoredUserSet.getLong("user_id"));
                ignoredUsers.add(ignoredUserSet.getLong("user_id"));
            }

            while (macroSet.next()){
                macros.put("%" + macroSet.getString("title"), macroSet.getString("description"));
            }
        }
        catch (SQLException e){
            System.out.println(e);
        }

        commands = loadCommandsFromFactories();
    }

    public Map<String, Command> loadCommandsFromFactories(){
        Map<String, Command> commandMap = new HashMap<>();

        TestCommands testCommands = new TestCommands();
        List<Command> commands = testCommands.buildCommandList();

        HelpCommands helpCommands = new HelpCommands();
        commands.addAll(helpCommands.buildCommandList());

        RedditCommands redditCommands = new RedditCommands();
        commands.addAll(redditCommands.buildCommandList());

        FunCommands funCommands = new FunCommands();
        commands.addAll(funCommands.buildCommandList());

        commands.forEach(command -> commandMap.put(prefix + command.getName().toLowerCase(), command));

        return commandMap;
    }

    public static List<String> getMacros() {
        return macros.keySet().stream()
                .map(s -> s.replace(prefix, ""))
                .collect(Collectors.toList());
    }

    public void runMethod(CommandSender sender, List<String> arguments)
    {
        //ignores user if user is under ignore threshold
        if(ignoredUsers.contains(sender.user.getIdLong()) && Validator.checkLevel(sender.guild,sender.user.getIdLong(), ignoreLevel) != 1){ return; }

        if(commands.get(arguments.get(0).toLowerCase()) != null)
        {
            String commandName = arguments.get(0);
            Command command = commands.get(commandName.toLowerCase());

            int validation = Validator.checkLevel(sender.guild,sender.user.getIdLong(), 0);
            if(validation == 0){
                sender.sendMessage("You need to have permission level " + "`" + 0 + "` " + "or higher to use this command");
                return;
            }
            else if(validation == 2){
                sender.sendMessage(App.jdaBot.getSelfUser().getName() + " is currently in lockdown, contact you local administrator.");
                return;
            }

            List<String> sanitizedArguments = Validator.checkIfValid(arguments, command, sender);

            if(sanitizedArguments == null)
            {
                timerCount++;
                if(!timerToggle){
                    rtfmTimer = new Thread(startTimer(15000));
                    rtfmTimer.setDaemon(true);
                    rtfmTimer.start();

                    timerToggle = true;
                }
                else if(timerCount >= 3 && timerToggle){
                    sender.sendMessage("for christ sake would you please READ THE MANUAL! `%help " + commandName.toLowerCase() + "`" + " :rage:");
                    timerCount = 0;
                    rtfmTimer = null;
                    return;
                }
                sender.sendMessage("yea no, thats not how you use that command, type " + "`%help " + commandName.toLowerCase().replace("%", "") + "`");
                return;
            }

            CommandEvent event = new CommandEvent(sender, sender.guild, sender.messageChannel, sender.user, sender.message, sanitizedArguments);
            command.execute(event);
        }
        else if(macros.containsKey(arguments.get(0))){
            sender.sendMessage(macros.get(arguments.get(0)));
        }
    }

    public Map<String, Command> getCommands(){
        return commands;
    }

    private Runnable startTimer(long time){
            return () -> {
                try {
                    Thread.sleep(time);
                    timerToggle = false;
                    timerCount = 0;
                }
                catch (InterruptedException e){
                    timerToggle = false;
                }};
    }


    public static void addMacro(String title, String description){
        macros.put(prefix + title, description);
    }

    public static void removeMacro(String title){
        macros.remove(prefix + title);
    }


    public void registerMethod(String commandName, Pair<Method,Object> pair)
    {
        methods.put(commandName, pair);
    }

    public void removeMethod(String commandName)
    {
        methods.remove(commandName);
    }


    public static List<Long> getIgnoredUsers() {
        return ignoredUsers;
    }

    public static void addIgnoredUser(Long id){
        ignoredUsers.add(id);
    }

    public static void removeIgnoredUser(Long id){
        ignoredUsers.remove(id);
    }

    public static void setIgnoreLevel(int lvl){
        ignoreLevel = lvl;
    }

    public static int getIgnoreLevel() {
        return ignoreLevel;
    }
}
