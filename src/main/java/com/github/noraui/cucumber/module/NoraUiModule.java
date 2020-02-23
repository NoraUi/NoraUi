/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.module;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.steps.Step;
import com.github.noraui.browser.steps.BrowserSteps;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.interceptor.ConditionedInterceptor;
import com.github.noraui.cucumber.interceptor.StepInterceptor;
import com.github.noraui.service.CryptoService;
import com.github.noraui.service.CucumberExpressionService;
import com.github.noraui.service.HttpService;
import com.github.noraui.service.ScreenService;
import com.github.noraui.service.UserNameService;
import com.github.noraui.service.impl.CryptoServiceImpl;
import com.github.noraui.service.impl.CucumberExpressionServiceImpl;
import com.github.noraui.service.impl.HttpServiceImpl;
import com.github.noraui.service.impl.ScreenServiceImpl;
import com.github.noraui.service.impl.UserNameServiceImpl;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

public class NoraUiModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiModule.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Binder binder) {
        LOGGER.debug("NORAUI interceptors binding");
        binder.bindInterceptor(any(), annotatedWith(Conditioned.class), new ConditionedInterceptor());
        binder.bindInterceptor(Matchers.subclassesOf(Step.class).or(Matchers.subclassesOf(BrowserSteps.class)), any(),
                new StepInterceptor());

        LOGGER.debug("NORAUI service binding");
        binder.bind(CryptoService.class).to(CryptoServiceImpl.class).asEagerSingleton();
        binder.bind(CucumberExpressionService.class).to(CucumberExpressionServiceImpl.class).asEagerSingleton();
        binder.bind(HttpService.class).to(HttpServiceImpl.class).asEagerSingleton();
        binder.bind(ScreenService.class).to(ScreenServiceImpl.class).asEagerSingleton();
        binder.bind(UserNameService.class).to(UserNameServiceImpl.class).asEagerSingleton();
    }
}
