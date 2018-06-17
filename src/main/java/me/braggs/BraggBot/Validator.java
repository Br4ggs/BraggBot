package me.braggs.BraggBot;

import me.braggs.BraggBot.Commands.CommandFramework.Command;
import me.braggs.BraggBot.Commands.CommandFramework.CommandArg;
import me.braggs.BraggBot.Commands.CommandFramework.CommandCategory;
import me.braggs.BraggBot.Commands.oldFramework.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Validator extends ValidatorMethods {
    private static Boolean performValidation(String arg, Commandarguments.paramType type) {
        return type.validateInput(arg);
    }

    public static List<String> checkIfValid(List<String> args, Command command, CommandSender sender) {
        if(command.getParameters() == null){
            System.out.println("no parameters are required for this command");
            return new ArrayList<>();
        }

        List<CommandArg> requiredTypes = Arrays.asList(command.getParameters());

        CommandCategory typeOfCommand = command.getCategory();

        if (!isUsableByType(typeOfCommand, sender)) {
            return null;
        }

        List<String> sanitizedList = sanitizeInput(args, requiredTypes);

        if (sanitizedList.size() != requiredTypes.size()) {
            return null;
        }

        //rework this for optional arguments
        for (int i = 0; i < requiredTypes.size(); i++) {
            if (!performValidation(sanitizedList.get(i), requiredTypes.get(i).type)) {
                return null;
            }
        }
        return sanitizedList;
    }

    //rework this to fill in optional arguments with default value if not provided
    private static List<String> sanitizeInput(List<String> args, List<CommandArg> parameters) {
        List<String> sanitizedList = new ArrayList<>();
        args.remove(0);

        System.out.println(args.size());
        System.out.println(parameters.size());

        for (int i = 0; i < parameters.size(); i++) {
            if (i >= args.size()) {
                System.out.println("an argument is missings in args");
                if(!parameters.get(i).optional){
                    return sanitizedList;
                }
                else {
                }
                sanitizedList.add(parameters.get(i).defaultArgument);
            }
            else {

                //this could be a separate method
                switch (parameters.get(i).type) {
                    case Sentence:
                        List<String> sublist = args.subList(i, args.size());
                        String sentence = String.join(" ", sublist);
                        sanitizedList.add(sentence);
                        break;
                    case TranslationLang:
                        sanitizedList.add(StringUtils.capitalize(args.get(i)));
                        break;
                    default:
                        sanitizedList.add(args.get(i));
                }
            }
        }
        return sanitizedList;
    }

    private static Boolean isUsableByType(CommandCategory type, CommandSender sender) {
        switch (type) {
            case voice:
                List<User> usersInVC = App.jdaBot.getVoiceChannelById(App.voiceCommands.voiceID).getMembers()
                        .stream()
                        .map(Member::getUser)
                        .collect(Collectors.toList());
                return usersInVC.contains(sender.user) || checkLevel(sender.guild, sender.user.getIdLong(), 2) == 1;
            default:
                return true;
        }
    }

    private static boolean indexExists(final List list, final int index) {
        return index >= 0 && index < list.size();
    }

    //0 == cannot use, 1 == can use, 2 == bot is in lockdown
    public static int checkLevel(Guild guild, long ID, int level) {
        if (ID == Long.parseLong(Config.getInstance().getSuperUserID())) {
            return 1;
        } else if (Config.getInstance().isLockDown()) {
            return 2;
        }
        List<String> rolesWithLevel = Config.getInstance().getRolesWithLevel(level);
        List<Role> filteredRoles = guild.getMemberById(ID).getRoles()
                .stream()
                .filter(role -> rolesWithLevel.contains(role.getName()))
                .collect(Collectors.toList());
        if (filteredRoles.size() > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
