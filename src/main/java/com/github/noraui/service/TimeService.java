/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

public interface TimeService {

    /**
     * @param offsetDay
     *            is number of day sub.
     * @param zone
     *            is String of ZoneId (ex: Europe/Paris)
     * @param formatter
     *            is an string ZonedDateTime formatter.
     * @return String of date
     */
    String getDayMinusXBusinessDay(int offsetDay, String zone, String formatter);

    /**
     * @param offsetDay
     *            is number of day added.
     * @param zone
     *            is String of ZoneId (ex: Europe/Paris)
     * @param formatter
     *            is an string ZonedDateTime formatter.
     * @return String of date
     */
    String getDayPlusXBusinessDay(int offsetDay, String zone, String formatter);

}
