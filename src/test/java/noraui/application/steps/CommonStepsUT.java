package noraui.application.steps;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cucumber.api.Scenario;
import noraui.application.page.Page;
import noraui.exception.FailureException;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinStepCondition;
import noraui.utils.Utilities;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
public class CommonStepsUT {

    private CommonSteps s = null;

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
        } catch (TechnicalException e) {
            Assert.assertEquals("Unable to retrieve Page with name: demo.DemoSteps", e.getMessage());
        } catch (FailureException a) {
            Assert.assertFalse("Erreur car checkMandatoryField ne doit trouver une TechnicalException", true);
        }
        try {
            s.checkMandatoryField("demo.DemoPage", "mockField", "text", new ArrayList<GherkinStepCondition>());
        } catch (FailureException a) {
            Assert.assertEquals("Élément non trouvé sur la page. [-mockField]", a.getMessage());
        }
    }

}
