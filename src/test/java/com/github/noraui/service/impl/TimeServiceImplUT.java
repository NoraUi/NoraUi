/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import static com.github.noraui.utils.Constants.DEFAULT_DATE_FORMAT;
import static com.github.noraui.utils.Constants.DEFAULT_ZONE_ID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.NoraUiLoggingInjector;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.TimeService;
import com.google.inject.Inject;

@Loggable
public class TimeServiceImplUT {

    static Logger log;

    @Inject
    private TimeService timeService;

    @Before
    public void setUp() throws TechnicalException {
        NoraUiInjector.resetInjector();
        NoraUiLoggingInjector.addInjector(this.getClass().getPackage().getName());
        new NoraUiInjectorSource().getInjector().injectMembers(this);
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testGetDayMinusOneBusinessDay() {
        String date = timeService.getDayMinusXBusinessDay(1, DEFAULT_ZONE_ID, DEFAULT_DATE_FORMAT);
        log.info(date);
    }

    @Test
    public void testGetDayPlusOneBusinessDay() {
        String date = timeService.getDayPlusXBusinessDay(1, DEFAULT_ZONE_ID, DEFAULT_DATE_FORMAT);
        log.info(date);
    }

}
