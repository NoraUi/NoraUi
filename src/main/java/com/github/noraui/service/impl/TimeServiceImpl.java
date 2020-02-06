/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;

import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.TimeService;
import com.google.inject.Singleton;

@Loggable
@Singleton
public class TimeServiceImpl implements TimeService {

    protected static Logger log;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDayMinusXBusinessDay(int offsetDay, String zone, String formatter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        ZonedDateTime date = ZonedDateTime.now(ZoneId.of(zone)).minusDays(offsetDay);
        if ("SATURDAY".equals(date.getDayOfWeek().toString())) {
            date = date.minusDays(1);
        } else if ("SUNDAY".equals(date.getDayOfWeek().toString())) {
            date = date.minusDays(2);
        }
        return date.format(dateTimeFormatter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDayPlusXBusinessDay(int offsetDay, String zone, String formatter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        ZonedDateTime date = ZonedDateTime.now(ZoneId.of(zone)).plusDays(offsetDay);
        if ("SATURDAY".equals(date.getDayOfWeek().toString())) {
            date = date.plusDays(2);
        } else if ("SUNDAY".equals(date.getDayOfWeek().toString())) {
            date = date.plusDays(1);
        }
        return date.format(dateTimeFormatter);
    }

}
