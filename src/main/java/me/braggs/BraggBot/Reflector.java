package me.braggs.BraggBot;

import javafx.util.Pair;
import me.braggs.BraggBot.Commands.CommandManager;
import me.braggs.BraggBot.Commands.oldFramework.CommandAnnotation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Reflector
{
    public static void addMethodsToManager(Method[] methods, Object className, CommandAnnotation.commandType commandType, CommandManager manager)
    {
        for (Method method : methods) {
            if (method.isAnnotationPresent(CommandAnnotation.class)) {
                if(method.getAnnotation(CommandAnnotation.class).typeOfCommand() != commandType){continue;}
                Pair<Method, Object> methodpair = new Pair<Method, Object>(method, className);
                manager.registerMethod("%" + method.getName().toLowerCase(), methodpair);
            }
        }
    }

    public static void removeMethodsFromManager (CommandAnnotation.commandType commandType, CommandManager manager)
    {
        Map<String, Pair<Method,Object>> methodList = new HashMap<>(CommandManager.methods);
        for (HashMap.Entry<String, Pair<Method,Object>> entry : methodList.entrySet()) {
            if (entry.getValue().getKey().getAnnotation(CommandAnnotation.class).typeOfCommand() == commandType) {
                manager.removeMethod(entry.getKey().toLowerCase());
            }
        }
    }
}
