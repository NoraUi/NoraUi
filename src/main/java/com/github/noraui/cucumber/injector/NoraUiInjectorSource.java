/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.module.NoraUiModule;
import com.github.noraui.exception.TechnicalException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import cucumber.api.guice.CucumberModules;
import cucumber.metrics.module.SpeedRegulatorModule;
import cucumber.metrics.module.TimeModule;
import cucumber.runtime.java.guice.InjectorSource;

public class NoraUiInjectorSource implements InjectorSource {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NoraUiInjectorSource.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public Injector getInjector() {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, CucumberModules.SCENARIO, new SpeedRegulatorModule(), new TimeModule(), new NoraUiModule());
        try {
            NoraUiInjector.createInjector(injector);
        } catch (TechnicalException e) {
            logger.error("error NoraUiInjectorSource.getInjector()", e);
        }
        return injector;
    }

}