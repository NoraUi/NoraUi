/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

@Loggable
public class TimeSteps extends Step {

    static Logger log;

    /**
     * @param targetKey
     *            is target key for the context.
     * @param params
     *            contains formatter ("yyyy-MM-dd" by default), zone ("Europe/London" by default)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("J\'initialise une date et je sauvegarde dans la clé {string} du contexte:")
    @And("I initialize a date and I save in {string} context key:")
    public void initDateInContext(String targetKey, Map<String, String> params) throws FailureException {
        try {
            String formatter = params.getOrDefault("formatter", "yyyy-MM-dd");
            String zone = params.getOrDefault("zone", "Europe/London");
            String date = ZonedDateTime.now(ZoneId.of(zone)).format(DateTimeFormatter.ofPattern(formatter));
            Context.saveValue(targetKey, date);
            Context.getCurrentScenario().write("SAVE " + targetKey + "=" + date);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * @param targetKey
     *            is target key for the context.
     * @param params
     *            contains formatter ("yyyy-MM-dd" by default), zone ("Europe/London" by default)
     *            and the list of possible add (plusNanos, plusSeconds, plusMinutes, plusHours, plusDays, plusWeeks, plusMonths).
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("J\'ajoute à la date et je sauvegarde dans la clé {string} du contexte:")
    @And("I add to date and I save in {string} context key:")
    public void foo(String targetKey, Map<String, String> params) throws FailureException {
        try {
            final String value = Context.getValue(targetKey);
            String formatter = params.getOrDefault("formatter", "yyyy-MM-dd");
            String zone = params.getOrDefault("zone", "Europe/London");
            String plusNanos = params.getOrDefault("plusNanos", "0");
            String plusSeconds = params.getOrDefault("plusSeconds", "0");
            String plusMinutes = params.getOrDefault("plusMinutes", "0");
            String plusHours = params.getOrDefault("plusHours", "0");
            String plusDays = params.getOrDefault("plusDays", "0");
            String plusWeeks = params.getOrDefault("plusWeeks", "0");
            String plusMonths = params.getOrDefault("plusMonths", "0");
            LocalDate localDate = LocalDate.parse(value);
            ZonedDateTime dateTime = localDate.atStartOfDay(ZoneId.of(zone));
            String after = dateTime.plusNanos(Integer.parseInt(plusNanos)).plusSeconds(Integer.parseInt(plusSeconds)).plusMinutes(Integer.parseInt(plusMinutes)).plusHours(Integer.parseInt(plusHours))
                    .plusDays(Integer.parseInt(plusDays)).plusWeeks(Integer.parseInt(plusWeeks)).plusMonths(Integer.parseInt(plusMonths)).format(DateTimeFormatter.ofPattern(formatter));
            Context.saveValue(targetKey, after);
            Context.getCurrentScenario().write("SAVE " + targetKey + "=" + after);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

}
