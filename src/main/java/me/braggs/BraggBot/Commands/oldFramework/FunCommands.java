package me.braggs.BraggBot.Commands.oldFramework;



import me.braggs.BraggBot.*;
import me.braggs.BraggBot.Commands.CommandArgs;
import me.braggs.BraggBot.Commands.CommandManager;
import me.braggs.BraggBot.Commands.oldFramework.rps.*;
import me.braggs.BraggBot.Commandarguments.paramType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FunCommands {
    int id;

    public FunCommands() {
        Random rnd = new Random();
        id = rnd.nextInt(1000000000);
    }

    public static String pathToContents(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    @CommandAnnotation
    public void testException(CommandSender sender, CommandArgs arguments){
        throw new ArithmeticException("This is a test");
    }

    @CommandAnnotation(arguments = {paramType.StringCommand, Commandarguments.paramType.String, Commandarguments.paramType.Sentence}, level = 3)
    public void addMacro(CommandSender sender, CommandArgs arguments){
        if(CommandManager.methods.containsKey("%" + arguments.getArgument(1)) || CommandManager.getMacros().contains(arguments.getArgument(1))){
            sender.sendMessage("no can do, theres already a command or macro with that name");
            return;
        }
        try {
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("INSERT INTO macros (title, description) VALUES (?,?)");

            ps.setString(1, arguments.getArgument(1));
            ps.setString(2, arguments.getArgument(2));
            ps.executeUpdate();

            CommandManager.addMacro(arguments.getArgument(1), arguments.getArgument(2));
            sender.sendMessage("**added:** " + arguments.getArgument(1) + " **with description:** " + arguments.getArgument(2) + " **to database**");
        }
        catch (SQLException e){
            System.out.println(e);
            sender.sendMessage("there already is a macro in my db with the name: " + arguments.getArgument(1));
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, paramType.String}, level = 3)
    public void removeMacro(CommandSender sender, CommandArgs arguments){
        if(!CommandManager.getMacros().contains(arguments.getArgument(1))){
            sender.sendMessage("slow down there cowboy, i dont even have a macro with that name");
            return;
        }
        try{
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("DELETE FROM macros WHERE title = ?");

            ps.setString(1, arguments.getArgument(1));
            ps.executeUpdate();

            CommandManager.removeMacro(arguments.getArgument(1));
            sender.sendMessage("**removed** " + arguments.getArgument(1) + " **from database**");
        }
        catch (SQLException e){
            System.out.println(e);
        }
    }

    @CommandAnnotation(level = 2)
    public void viewMacros(CommandSender sender, CommandArgs arguments){
        try{
            PreparedStatement ps = DatabaseManager.Singleton.INSTANCE.getInstance().getConnection()
                    .prepareStatement("SELECT title FROM macros");

            ResultSet rs = ps.executeQuery();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Available macros:");

            StringBuilder builder = new StringBuilder();
            while (rs.next()){
                builder.append(rs.getString("title") + ", ");
            }
            embed.setDescription(builder);
            sender.sendEmbed(embed);
        }
        catch (SQLException e){
            System.out.println(e);
        }
    }

    @CommandAnnotation
    public void id(CommandSender sender, CommandArgs arguments) {
        sender.sendMessage(Integer.toString(id));
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, paramType.String}, requiredArgs = 0, defArgs = "",level = 1)
    public void searchVC(CommandSender sender, CommandArgs arguments) {
        String name = "";
        for (int i = 1; i < arguments.getSize(); i++) {
            name += arguments.getArgument(i).toLowerCase();
            if (i < arguments.getSize() - 1) {
                name += " ";
            }
        }

        List<VoiceChannel> channel = sender.guild.getVoiceChannels();
        List<VoiceChannel> results = new ArrayList<>();

        for (VoiceChannel ch : channel) {
            if (ch.getName().toLowerCase().contains(name)) {
                results.add(ch);
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Search results for: " + name + " | " + results.size());
        embed.setColor(Color.MAGENTA);

        for (VoiceChannel id : results) {
            embed.addField(id.getName(), id.getId(), true);
        }
        sender.sendEmbed(embed);
    }

    @CommandAnnotation(level = 1)
    public void rtd(CommandSender sender, CommandArgs arguments) {
        int number = (int) (java.lang.Math.random() * 6) + 1;
        sender.sendMessage("You rolled: " + number);
        if (number > 5) {
            sender.sendMessage("Lucky you ;)");
        }
    }

    @CommandAnnotation(level = 1)
    public void ping(CommandSender sender, CommandArgs arguments) {
        sender.sendMessage("pong");
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Sentence},requiredArgs = 0, defArgs = "gah you silly, next time you should provide an argument", level = 3)
    public void echo(CommandSender sender, CommandArgs arguments){
        sender.sendMessage(arguments.getArgument(1));
    }

    @CommandAnnotation(level = 1)
    public void info(CommandSender sender, CommandArgs arguments) {
        EmbedBuilder message = new EmbedBuilder()
                .setThumbnail(App.jdaBot.getSelfUser().getAvatarUrl())
                .setTitle("Well hello there! \uD83D\uDC4B")
                .setDescription("Hellooow! My name is braggbot and im a bot, bleep! bloop! Btw, im written in java.")
                .setColor(Color.orange)
                .addField("Server Owner", sender.guild.getOwner().getUser().getName(), true)
                .addField("Bot owner", App.jdaBot.getUserById(Config.getInstance().getSuperUserID()).getName(), true)
                .addField("Members", String.valueOf(sender.guild.getMembers().size()), true)
                .addField("Currently available commands:", "", false);
        Map<CommandAnnotation.commandType,String> commandTypes = new HashMap<>();
        for (String key : CommandManager.methods.keySet()) {
            CommandAnnotation.commandType type = CommandManager.methods.get(key).getKey().getAnnotation(CommandAnnotation.class).typeOfCommand();
            if(!commandTypes.containsKey(type)){
              commandTypes.put(type, "");
            }
            String command = commandTypes.get(type) + ("`" + key.replace("%", "") + "`" + ",");
            commandTypes.replace(type, command);
        }
        sortByValue(commandTypes).forEach((commandType, string) -> message.addField(commandType.toString(), string, true));
        message.addField("wanna find out more?", "to find out more about a certain command, user `%help <command>`", false);
        sender.sendEmbed(message);
    }

    @CommandAnnotation(level = 3)
    public void copyPasta(CommandSender sender, CommandArgs arguments) {
        try (Stream<Path> paths = Files.walk(Paths.get(ResourceFinder.getResource("copyPastas")))) {
            List<String> copyPastas = paths
                    .filter(Files::isRegularFile)
                    .map(FunCommands::pathToContents)
                    .collect(Collectors.toList());

            int i;
            Random ran = new Random();
            i = ran.nextInt(copyPastas.size());
            System.out.println(i);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setDescription(copyPastas.get(i));
            embed.setColor(Color.BLUE);
            sender.sendEmbed(embed);
        } catch (java.io.IOException e) {
            System.out.println(e);
        }
    }

    @CommandAnnotation(level = 1)
    public void hello(CommandSender sender, CommandArgs arguments) {
        try {
            List<String> hello = Files.readAllLines(Paths.get(ResourceFinder.getResource("helloResponses.txt")));
            int i;
            Random ran = new Random();
            i = ran.nextInt(hello.size());
            String hellomessage = hello.get(i).replace("%NAME%", sender.user.getAsMention());
            sender.sendMessage(hellomessage);
        } catch (java.io.IOException e) {
            System.out.println(e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, paramType.String}, requiredArgs = 0, defArgs = "help", level = 1)
    public void help(CommandSender sender, CommandArgs arguments) {
        try {
            List<String> resource = Files.readAllLines(Paths.get(ResourceFinder.getResource("helpDescriptions.txt")));
            for (String line : resource) {
                if(line.isEmpty()){
                    continue;
                }

                if (line.substring(0, line.indexOf("|")).toLowerCase().equals(arguments.getArgument(1).toLowerCase())) {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle(arguments.getArgument(1));
                    embed.setDescription((line.replace(arguments.getArgument(1).toUpperCase() + "|", " ")));
                    embed.setColor(Color.BLUE);
                    sender.sendEmbed(embed);
                    return;
                }
            }
            sender.sendMessage(arguments.getArgument(1) + " is not in my current `help` index");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.String},level = 1)
    public void rps(CommandSender sender, CommandArgs arguments) {
        String argument = arguments.getArgument(1);
        switch (argument) {
            case "start":
                if (RockPaperScissors.instance == null) {
                    sender.sendMessage("starting rock paper scissors");
                    new RockPaperScissors();
                }
                else {
                    sender.sendMessage("game already started");
                }
                break;
            case "exit":
                if (RockPaperScissors.instance != null) {
                    sender.sendMessage("exiting rock paper scissors");
                    RockPaperScissors.instance.endSession();
                }
                else {
                    sender.sendMessage("no game is currently running");
                }
                break;
            default:
                sender.sendMessage("thats not valid");
        }
    }

    @CommandAnnotation(arguments = {Commandarguments.paramType.StringCommand, Commandarguments.paramType.Integer, Commandarguments.paramType.MathOperator, Commandarguments.paramType.Integer}, level = 1)
    public void quickMath(CommandSender sender, CommandArgs arguments) {
        double arg1 = Double.parseDouble(arguments.getArgument(1));
        double arg2 = Double.parseDouble(arguments.getArgument(3));

        double answer;
        Number answerMinus;
        Math calc = new Math();
        switch (arguments.getArgument(2)) {
            case "+":
                answer = calc.mathContainer(arg1, arg2, Math::add);
                answerMinus = calc.mathContainer(answer, 1, Math::subtract);
                sender.sendMessage("YOW, " + answer + " MINUS ONE THATS " + answerMinus.toString() + " QUICK MATHS!");
                break;
            case "-":
                answer = calc.mathContainer(arg1, arg2, Math::subtract);
                answerMinus = calc.mathContainer(answer, 1, Math::subtract);
                sender.sendMessage("BOOM, " + answer + " MINUS ONE THATS " + answerMinus.toString() + " QUICK MATHS!");
                break;
            case "*":
                answer = calc.mathContainer(arg1, arg2, Math::multiply);
                answerMinus = calc.mathContainer(answer, 1, Math::subtract);
                sender.sendMessage("GAH, " + answer + " MINUS ONE THATS " + answerMinus.toString() + " QUICK MATHS!");
                break;
            case "/":
                answer = calc.mathContainer(arg1, arg2, Math::divide);
                answerMinus = calc.mathContainer(answer, 1, Math::subtract);
                sender.sendMessage("SKRAAA, " + answer + " MINUS ONE THATS " + answerMinus.toString() + " QUICK MATHS!");
                break;
        }
    }
}

class Math {
    public double mathContainer(double n1, double n2, BiFunction<Double, Double, Double> consumer) {
        System.out.println(consumer.apply(n1, n2));
        return consumer.apply(n1, n2);
    }

    public static double add(Number a, Number b) {
        return a.doubleValue() + b.doubleValue();
    }

    public static double subtract(Number a, Number b) {
        return a.doubleValue() - b.doubleValue();
    }

    public static double multiply(Number a, Number b) {
        return a.doubleValue() * b.doubleValue();
    }

    public static double divide(Number a, Number b) {
        return a.doubleValue() / b.doubleValue();
    }
}