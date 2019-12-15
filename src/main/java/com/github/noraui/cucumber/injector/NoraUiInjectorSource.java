/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.injector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.github.noraui.cucumber.metrics.module.SpeedRegulatorModule;
import com.github.noraui.cucumber.metrics.module.TimeModule;
import com.github.noraui.cucumber.module.NoraUiModule;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import cucumber.api.guice.CucumberModules;
import cucumber.runtime.java.guice.InjectorSource;

@Loggable
public class NoraUiInjectorSource implements InjectorSource {

    static Logger log;

    protected Stage stage = Stage.PRODUCTION;
    protected List<Module> noraUiModules = new ArrayList<>(Arrays.asList(CucumberModules.createScenarioModule(), new SpeedRegulatorModule(), new TimeModule(), new NoraUiModule()));

    /**
     * {@inheritDoc}
     */
    @Override
    public Injector getInjector() {
        Injector injector = Guice.createInjector(stage, noraUiModules);
        try {
            NoraUiInjector.createInjector(injector);
        } catch (TechnicalException e) {
            log.error("NoraUiInjectorSource.getInjector()", e);
        }
        return injector;
    }

}
