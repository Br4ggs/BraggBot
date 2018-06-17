package me.braggs.BraggBot;

import me.braggs.BraggBot.Commands.CommandManager;
import org.apache.commons.validator.routines.UrlValidator;
import java.util.Arrays;

public class ValidatorMethods {

    static boolean isValidTransLang(String arg){
        return App.fetchCommands.languages.languageMap.containsKey(arg);
    }

    static boolean isValidLong(String arg){
        try{
            Long.parseLong(arg);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    static boolean isNotTooLong(String arg){
        return (arg.toCharArray().length <= 700);
    }

    static boolean isValidInteger(String arg){
        try{
            Integer.parseInt(arg);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    static boolean isValidRedditSorting(String arg){
        String[] sorting = getNames(net.dean.jraw.models.SearchSort.class);

        if(Arrays.toString(sorting).contains(arg.toUpperCase())){
            return true;
        }
        else return false;
    }

    static boolean isValidRedditTime(String arg){
        String[] time = getNames(net.dean.jraw.models.TimePeriod.class);

        if(Arrays.toString(time).contains(arg.toUpperCase())){
            return true;
        }
        else return false;
    }

    private static String[] getNames(Class<? extends Enum<?>> e)
    {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    static boolean isValidURL(String arg){
        if(UrlValidator.getInstance().isValid(arg)){ return true;}
        else return false;
    }

    static boolean isExistingCommand(String arg)
    {
        System.out.println(arg);
        if(CommandManager.Singleton.INSTANCE.getInstance().getCommands().get(arg) != null) { return true;}
        else return false;
    }

    static boolean isOperator(String arg)
    {
        if(arg.matches("\\+|-|/|\\*")){ return true;}
        else return false;
    }
}
