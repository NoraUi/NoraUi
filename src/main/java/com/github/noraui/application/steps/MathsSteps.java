/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.Constants.PREFIX_SAVE;

import java.util.Map;

import org.slf4j.Logger;

import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

import io.cucumber.java.en.And;
import io.cucumber.java.fr.Et;

/**
 * @since 4.2.3
 */
@Loggable
public class MathsSteps extends Step {

    static Logger log;

    public static final String FORMAT = "format";
    public static final String DEFAULT_FORMAT = "Integer";

    /**
     * @since 4.2.3
     * @param targetKey
     *            is target key for the context.
     * @param params
     *            contains format ("Integer" by default)
     *            and the list of possible operator (plus, minus, multiplies, divides)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("Je change la valeur et je sauvegarde dans la clé {string} du contexte:")
    @And("I change value and I save in {string} context key:")
    public void changeValue(String targetKey, Map<String, String> params) throws FailureException {
        try {
            String after = "";
            String format = params.getOrDefault(FORMAT, DEFAULT_FORMAT);
            String plus = params.getOrDefault("plus", "0");
            String minus = params.getOrDefault("minus", "0");
            String multiplies = params.getOrDefault("multiplies", "1");
            String divides = params.getOrDefault("divides", "1");
            switch (format) {
                case DEFAULT_FORMAT:
                    int value = Integer.parseInt(Context.getValue(targetKey));
                    value += Integer.parseInt(plus);
                    value -= Integer.parseInt(minus);
                    value = value * Integer.parseInt(multiplies);
                    value = value / Integer.parseInt(divides);
                    after = String.valueOf(value);
                    break;
                default:
                    new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_FORMAT), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
                    break;
            }
            Context.saveValue(targetKey, after);
            Context.getCurrentScenario().write(PREFIX_SAVE + targetKey + "=" + after);
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.getMessage(Messages.FAIL_MESSAGE_UNABLE_TO_CHANGE_VALUE), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

}
