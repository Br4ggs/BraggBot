package me.braggs.BraggBot;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.function.Predicate;

public class Commandarguments extends ValidatorMethods {
    public enum paramType
    {
        Number(NumberUtils::isNumber),
        Integer(Commandarguments::isValidInteger),
        Long(Commandarguments::isValidLong),
        Boolean(BooleanUtils::toBoolean),
        String(StringUtils::isAsciiPrintable),
        MathOperator(Commandarguments::isOperator),
        StringCommand(Commandarguments::isExistingCommand),
        Url(Validator::isValidURL),
        RedditSortingType(Commandarguments::isValidRedditSorting),
        RedditTimePeriod(Commandarguments::isValidRedditTime),
        TranslationLang(Commandarguments::isValidTransLang),
        Sentence(Commandarguments::isNotTooLong);

        private Predicate<String> validation;

        paramType(Predicate<String> validation) {
            this.validation = validation;
        }

        public boolean validateInput(String arg) {
            return validation.test(arg);
        }
    }
}
