package noraui.cucumber.module;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import org.apache.log4j.Logger;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

import noraui.browser.steps.BrowserSteps;
import noraui.cucumber.annotation.Conditioned;
import noraui.cucumber.interceptor.ConditionedInterceptor;
import noraui.cucumber.interceptor.StepInterceptor;

public class NoraUiModule implements Module {

    private static Logger logger = Logger.getLogger(NoraUiModule.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Binder binder) {
        logger.debug("NORAUI NoraUiModule configure");
        binder.bindInterceptor(any(), annotatedWith(Conditioned.class), new ConditionedInterceptor());
        binder.bindInterceptor(Matchers.subclassesOf(noraui.application.steps.Step.class).or(Matchers.subclassesOf(BrowserSteps.class)), any(), new StepInterceptor());
    }
}
