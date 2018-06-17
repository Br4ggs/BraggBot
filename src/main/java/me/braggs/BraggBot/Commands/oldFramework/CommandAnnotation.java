package me.braggs.BraggBot.Commands.oldFramework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.braggs.BraggBot.Commandarguments.paramType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommandAnnotation
{
    enum commandType{
        fun,
        rps,
        fetch,
        voice,
        mod
    }
    paramType[] arguments() default paramType.StringCommand;
    int requiredArgs() default 10;
    String[] defArgs() default "NULL";
    commandType typeOfCommand() default  commandType.fun;
    int level() default 5;
}
