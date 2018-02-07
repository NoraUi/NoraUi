/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.module;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.interceptor.ConditionedInterceptor;
import com.github.noraui.cucumber.interceptor.StepInterceptor;
import com.github.noraui.service.ScreenService;
import com.github.noraui.service.UserNameService;
import com.github.noraui.service.impl.ScreenServiceImpl;
import com.github.noraui.service.impl.UserNameServiceImpl;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

public class NoraUiModule implements Module {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NoraUiModule.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Binder binder) {
        logger.debug("NORAUI NoraUiModule configure");
        binder.bindInterceptor(any(), annotatedWith(Conditioned.class), new ConditionedInterceptor());
        binder.bindInterceptor(Matchers.subclassesOf(com.github.noraui.application.steps.Step.class).or(Matchers.subclassesOf(BrowserSteps.class)), any(), new StepInterceptor());

        logger.debug("NORAUI service binding");
        binder.bind(ScreenService.class).to(ScreenServiceImpl.class).asEagerSingleton();
        binder.bind(UserNameService.class).to(UserNameServiceImpl.class).asEagerSingleton();
    }
}
