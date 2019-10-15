/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.injector;

import org.slf4j.Logger;

import com.github.noraui.cucumber.metrics.module.SpeedRegulatorModule;
import com.github.noraui.cucumber.metrics.module.TimeModule;
import com.github.noraui.cucumber.module.NoraUiModule;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import io.cucumber.guice.CucumberModules;
import io.cucumber.guice.InjectorSource;

@Loggable
public class NoraUiInjectorSource implements InjectorSource {

    static Logger log;

    /**
     * {@inheritDoc}
     */
    @Override
    public Injector getInjector() {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, CucumberModules.createScenarioModule(), new SpeedRegulatorModule(), new TimeModule(), new NoraUiModule());
        try {
            NoraUiInjector.createInjector(injector);
        } catch (TechnicalException e) {
            log.error("NoraUiInjectorSource.getInjector()", e);
        }
        return injector;
    }

}

