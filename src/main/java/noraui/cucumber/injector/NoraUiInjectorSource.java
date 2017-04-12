package noraui.cucumber.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import cucumber.api.guice.CucumberModules;
import cucumber.metrics.module.SpeedRegulatorModule;
import cucumber.metrics.module.TimeModule;
import cucumber.runtime.java.guice.InjectorSource;
import noraui.cucumber.module.NoraUiModule;
import noraui.utils.Context;

public class NoraUiInjectorSource implements InjectorSource {

    /**
     * {@inheritDoc}
     */
    @Override
    public Injector getInjector() {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, CucumberModules.SCENARIO, new SpeedRegulatorModule(), new TimeModule(), new NoraUiModule());
        Context.setNoraUiInjectorSource(injector);
        return injector;
    }

}