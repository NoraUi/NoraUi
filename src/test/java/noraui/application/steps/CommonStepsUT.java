package noraui.application.steps;

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

import cucumber.api.Scenario;
import noraui.application.page.Page;
import noraui.cucumber.injector.NoraUiInjector;
import noraui.cucumber.injector.NoraUiInjectorSource;
import noraui.exception.FailureException;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinStepCondition;
import noraui.utils.Messages;
import noraui.utils.Utilities;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ Utilities.class })
public class CommonStepsUT {

    private CommonSteps s = null;

    @Before
    public void setUp() {
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

        Page.setPageMainPackage("noraui.application.page.");

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
