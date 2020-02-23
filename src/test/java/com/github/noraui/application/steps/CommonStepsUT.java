/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
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

import com.github.noraui.application.page.Page;
import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Messages;

public class CommonStepsUT {

    private CommonSteps s;

    @Before
    public void setUp() {
        s = new CommonSteps();
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testPageUnableToRetrieve() throws TechnicalException {
        try {
            s.checkMandatoryField(Page.getInstance("demo.DemoSteps").getPageElementByKey("-mockField"), "text", new ArrayList<GherkinStepCondition>());
        } catch (final TechnicalException e) {
            Assert.assertEquals(String.format(Messages.getMessage("UNABLE_TO_RETRIEVE_PAGE"), "demo.DemoSteps"), e.getMessage());
        } catch (final FailureException a) {
            Assert.assertFalse("checkMandatoryField must return TechnicalException", true);
        }
    }

    @Test
    public void testErrorDuringPageElementLookup() throws TechnicalException {

        try {
            s.checkMandatoryField(Page.getInstance("PageWithPrivatePageElement").getPageElementByKey("-privateElement"), "text", new ArrayList<GherkinStepCondition>());
        } catch (final TechnicalException e) {
            Assert.assertEquals(String.format(Messages.getMessage("ERROR_DURING_PAGE_ELEMENT_LOOKUP"), "-privateElement"), e.getMessage());
        } catch (final FailureException a) {
            Assert.assertFalse("checkMandatoryField must return TechnicalException", true);
        }
    }

    @Test
    public void testFailMessageUnableToFindElement() throws TechnicalException {
        try {
            s.checkMandatoryField(Page.getInstance("bakery.DemoPage").getPageElementByKey("-mockField"), "text", new ArrayList<GherkinStepCondition>());
        } catch (final FailureException a) {
            Assert.assertEquals(Messages.getMessage("FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT") + " [-mockField]", a.getMessage());
        }
    }

}
