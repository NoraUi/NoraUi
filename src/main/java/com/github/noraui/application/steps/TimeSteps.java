/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import static com.github.noraui.utils.Constants.DEFAULT_DATE_FORMAT;
import static com.github.noraui.utils.Constants.DEFAULT_ZONE_ID;
import static com.github.noraui.utils.Constants.PREFIX_SAVE;

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
import com.github.noraui.service.TimeService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.google.inject.Inject;

import io.cucumber.java.en.And;
import io.cucumber.java.fr.Et;

@Loggable
public class TimeSteps extends Step {

    static Logger log;

    public static final String FORMATTER = "formatter";

    @Inject
    private TimeService timeService;

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
            String formatter = params.getOrDefault(FORMATTER, DEFAULT_DATE_FORMAT);
            String zone = params.getOrDefault("zone", DEFAULT_ZONE_ID);
            String date = ZonedDateTime.now(ZoneId.of(zone)).format(DateTimeFormatter.ofPattern(formatter));
            Context.saveValue(targetKey, date);
            Context.getCurrentScenario().write(PREFIX_SAVE + targetKey + "=" + date);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Et("J\'initialise une date avec aujourd\'hui plus {int} en jour ouvrés et je sauvegarde dans la clé {string} du contexte:")
    @And("I initialize a date with today plus {int} in business day and I save in {string} context key:")
    public void initializeBusinessDayWithAdd(int offsetDay, String targetKey, Map<String, String> params) throws FailureException {
        try {
            String formatter = params.getOrDefault(FORMATTER, DEFAULT_DATE_FORMAT);
            String zone = params.getOrDefault("zone", DEFAULT_ZONE_ID);
            String date = timeService.getDayPlusXBusinessDay(offsetDay, zone, formatter);
            Context.saveValue(targetKey, date);
            Context.getCurrentScenario().write(PREFIX_SAVE + targetKey + "=" + date);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    @Et("J\'initialise une date avec aujourd\'hui moins {int} en jours ouvrés et je sauvegarde dans la clé {string} du contexte:")
    @And("I initialize a date with today minus {int} in business days and I save in {string} context key:")
    public void initializeBusinessDayWithMinus(int offsetDay, String targetKey, Map<String, String> params) throws FailureException {
        try {
            String formatter = params.getOrDefault(FORMATTER, DEFAULT_DATE_FORMAT);
            String zone = params.getOrDefault("zone", DEFAULT_ZONE_ID);
            String date = timeService.getDayMinusXBusinessDay(offsetDay, zone, formatter);
            Context.saveValue(targetKey, date);
            Context.getCurrentScenario().write(PREFIX_SAVE + targetKey + "=" + date);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

    /**
     * @param targetKey
     *            is target key for the context.
     * @param params
     *            contains formatter ("yyyy-MM-dd" by default), zone ("Europe/London" by default)
     *            and the list of possible add (plusNanos, plusSeconds, plusMinutes, plusHours, plusDays, plusWeeks, plusMonths)
     *            and the list of possible sub (minusNanos, minusSeconds, minusMinutes, minusHours, minusDays, minusWeeks, minusMonths).
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Et("Je change la date et je sauvegarde dans la clé {string} du contexte:")
    @And("I change date and I save in {string} context key:")
    public void changeDate(String targetKey, Map<String, String> params) throws FailureException {
        try {
            final String value = Context.getValue(targetKey);
            String formatter = params.getOrDefault(FORMATTER, DEFAULT_DATE_FORMAT);
            String zone = params.getOrDefault("zone", DEFAULT_ZONE_ID);
            String plusNanos = params.getOrDefault("plusNanos", "0");
            String plusSeconds = params.getOrDefault("plusSeconds", "0");
            String plusMinutes = params.getOrDefault("plusMinutes", "0");
            String plusHours = params.getOrDefault("plusHours", "0");
            String plusDays = params.getOrDefault("plusDays", "0");
            String plusWeeks = params.getOrDefault("plusWeeks", "0");
            String plusMonths = params.getOrDefault("plusMonths", "0");
            String minusNanos = params.getOrDefault("minusNanos", "0");
            String minusSeconds = params.getOrDefault("minusSeconds", "0");
            String minusMinutes = params.getOrDefault("minusMinutes", "0");
            String minusHours = params.getOrDefault("minusHours", "0");
            String minusDays = params.getOrDefault("minusDays", "0");
            String minusWeeks = params.getOrDefault("minusWeeks", "0");
            String minusMonths = params.getOrDefault("minusMonths", "0");
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern(formatter));
            ZonedDateTime dateTime = localDate.atStartOfDay(ZoneId.of(zone));
            dateTime = dateTime.plusNanos(Integer.parseInt(plusNanos)).plusSeconds(Integer.parseInt(plusSeconds)).plusMinutes(Integer.parseInt(plusMinutes)).plusHours(Integer.parseInt(plusHours))
                    .plusDays(Integer.parseInt(plusDays)).plusWeeks(Integer.parseInt(plusWeeks)).plusMonths(Integer.parseInt(plusMonths)).minusNanos(Integer.parseInt(minusNanos))
                    .minusSeconds(Integer.parseInt(minusSeconds)).minusMinutes(Integer.parseInt(minusMinutes)).minusHours(Integer.parseInt(minusHours)).minusDays(Integer.parseInt(minusDays))
                    .minusWeeks(Integer.parseInt(minusWeeks)).minusMonths(Integer.parseInt(minusMonths));
            String after = dateTime.format(DateTimeFormatter.ofPattern(formatter));
            Context.saveValue(targetKey, after);
            Context.getCurrentScenario().write(PREFIX_SAVE + targetKey + "=" + after);
        } catch (Exception e) {
            new Result.Failure<>("", Messages.getMessage(Messages.FAIL_MESSAGE_DATE_FORMATTER), false, Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
        }
    }

}
