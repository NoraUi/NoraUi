/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.noraui.application.page.Page;
import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;

import cucumber.api.Scenario;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ Utilities.class })
public class CommonStepsUT {

    private CommonSteps s = null;

    @Before
    public void setUp() {
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void checkMandatoryFieldTextTypeTest() throws TechnicalException {
        // prepare mock
        PowerMockito.spy(Utilities.class);
        PowerMockito.doNothing().when(Utilities.class);
        Utilities.takeScreenshot(Matchers.any(Scenario.class));

        Page.setPageMainPackage("com.github.noraui.application.page.");

        // run test
        s = new CommonSteps();
        try {
            s.checkMandatoryField("demo.DemoSteps", "mockField", "text", new ArrayList<GherkinStepCondition>());
        } catch (final TechnicalException e) {
            Assert.assertEquals(String.format(Messages.getMessage("PAGE_UNABLE_TO_RETRIEVE"), "demo.DemoSteps"), e.getMessage());
        } catch (final FailureException a) {
            Assert.assertFalse("checkMandatoryField must return TechnicalException", true);
        }
        try {
            s.checkMandatoryField("demo.DemoPage", "mockField", "text", new ArrayList<GherkinStepCondition>());
        } catch (final FailureException a) {
            Assert.assertEquals(Messages.getMessage("FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT") + " [-mockField]", a.getMessage());
        }
    }

}
