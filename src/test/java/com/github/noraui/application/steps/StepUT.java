package com.github.noraui.application.steps;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.application.page.demo.DemoPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.cucumber.injector.NoraUiInjector;
import com.github.noraui.cucumber.injector.NoraUiInjectorSource;
import com.github.noraui.cucumber.interceptor.ConditionedInterceptor;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

public class StepUT {

    public static final String DEMO_PAGE_NAME = "demo.DemoPage";

    private Step step;
    private ConditionedInterceptor ci;
    private GherkinStepCondition gherkinCondition;
    private List<GherkinStepCondition> conditions;

    public static final String DO_NOT_MATCH_STRING = "^(?!.*IPADSL|.*C2E|.*LL2048).*$";
    public static final String CONTAINS_STRING = "^(.*Accès L2ETH|.*Accès L2TP).*$";
    public static final String START_STRING = "^\\[\\{\"action\":\"Qualif OSM\".*";

    @Before
    public void setUp() {
        step = new Step();
        ci = new ConditionedInterceptor();
        gherkinCondition = new GherkinStepCondition();
        conditions = new ArrayList<>();
        NoraUiInjector.resetInjector();
        new NoraUiInjectorSource().getInjector();
    }

    @After
    public void tearDown() {
        NoraUiInjector.resetInjector();
    }

    @Test
    public void testCheckConditionsEqualsIgnoreCase() {
        gherkinCondition.setExpected("Oui");
        gherkinCondition.setActual("OUi");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsEquals() {
        gherkinCondition.setExpected("Oui");
        gherkinCondition.setActual("Oui");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseEquals() {
        gherkinCondition.setExpected("Oui");
        gherkinCondition.setActual("Non");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsNotNull() {
        gherkinCondition.setExpected("^$");
        gherkinCondition.setActual("");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseNotNull() {
        gherkinCondition.setExpected("^$");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsNotEmpty() {
        gherkinCondition.setExpected(".+");
        gherkinCondition.setActual("stub");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseNotEmpty() {
        gherkinCondition.setExpected(".+");
        gherkinCondition.setActual("");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsDoNotMatchString() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stub");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stub IPADSL");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSLIgnoreCase() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stub IpAdsL");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL2() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubIPADSL");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL3() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubIPADSL ");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL4() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("IPADSLstub");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL5() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("IPADSL");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringC2E() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stub C2E");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringC2E2() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubC2E");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringC2E3() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubC2E ");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringC2E4() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("C2Estub");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringC2E5() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("C2E");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringLL2048() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stub LL2048");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringLL20482() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubLL2048");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringLL20483() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("stubLL2048 ");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringLL20484() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("LL2048stub");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringLL20485() {
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("LL2048");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsDoNotMatchStringInContext() {
        Context.saveValue("-stubKey", "stub");
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringInContext() {
        Context.saveValue("-stubKey", "stub IPADSL");
        gherkinCondition.setExpected(DO_NOT_MATCH_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsContainsString() {
        Context.saveValue("-stubKey", "stub Accès L2ETH");
        gherkinCondition.setExpected(CONTAINS_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsContainsString2() {
        Context.saveValue("-stubKey", "stub Accès L2TP & Accès L2ATM");
        gherkinCondition.setExpected(CONTAINS_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsDoesNotContainsString() {
        Context.saveValue("-stubKey", "stub Accès L2ATM");
        gherkinCondition.setExpected(CONTAINS_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertFalse(ci.checkConditions(conditions));
    }

    @Test
    public void testCheckConditionsStartString() {
        Context.saveValue("-stubKey",
                "[{\"action\":\"Qualif OSM\",\"stepToBeProcessed\":\"Migration Flux Clients\",\"actionOfStepToBeProcessed\":\"à annuler\"},{\"action\":\"Editer qualif OSM\",\"stepToBeProcessed\":\"Activation Supervision\",\"actionOfStepToBeProcessed\":\"à annuler\"},{\"action\":\"Editer qualif OSM\",\"stepToBeProcessed\":\"Transfert OT\",\"actionOfStepToBeProcessed\":\"à réaliser\"}]");
        gherkinCondition.setExpected(START_STRING);
        gherkinCondition.setActual("-stubKey");
        conditions.add(gherkinCondition);
        Assert.assertTrue(ci.checkConditions(conditions));
    }

    @Test
    public void testFormatMessage() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("-input_select_field");
            final String a = Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
            Assert.assertEquals("", "Message Input Select field in demo.", a);
        } catch (final TechnicalException e) {
            Assert.assertFalse("Error", true);
        }
    }

    @Test
    public void testFormatMessage2() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("-submit");
            final String a = Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
            Assert.assertEquals("", "Message Submit button in demo.", a);
        } catch (final TechnicalException e) {
            Assert.assertFalse("Error", true);
        }
    }

    @Test
    public void testFormatMessageNullPageElement() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("fake");
            Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
        } catch (final TechnicalException e) {
            Assert.assertEquals("TechnicalException found", "Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNullMessage() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format(null, pageElement, demoPage.getApplication());
        } catch (final TechnicalException e) {
            Assert.assertEquals("TechnicalException found", Messages.getMessage("FAIL_MESSAGE_FORMAT_STRING"), e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNotValidMessage() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format("Message %s in %s.%s", pageElement, demoPage.getApplication());
        } catch (final TechnicalException e) {
            Assert.assertEquals("TechnicalException found", Messages.getMessage("FAIL_MESSAGE_FORMAT_STRING"), e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNotValid2Message() {
        try {
            final DemoPage demoPage = (DemoPage) Page.getInstance(DEMO_PAGE_NAME);
            final PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format("Message %s.", pageElement, demoPage.getApplication());
        } catch (final TechnicalException e) {
            Assert.assertEquals("TechnicalException found", Messages.getMessage("FAIL_MESSAGE_FORMAT_STRING"), e.getMessage());
        }
    }

    @Test
    public void testRunAllStepsInLoopWithUndefinedStep() {
        final List<GherkinConditionedLoopedStep> steps = new ArrayList<>();
        final String expected = ".+;(ETS Backbone VLAN LL2048K\\|ETS Accès L2ETH\\|Accès XDSL ETS\\|Backbone VLAN Virtuelle)";
        final String actual = "VPNtechnique;OSC_ACC-resource_type";
        Context.saveValue("OSC_ACC-resource_type", "ETS Accès L2ETH");
        final GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        steps.add(gherkinConditionedLoopedStep);
        try {
            step.runAllStepsInLoop(steps);
            Assert.fail("TechnicalException should have been thrown");
        } catch (final TechnicalException e) {
            Assert.assertEquals(e.getMessage(), String.format(Messages.getMessage("TECHNICAL_ERROR_STEP_UNDEFINED"), "I wait '4' seconds."));
        }
    }

    // TODO ajouter un test sur les loop en lancant une méthode qui existe dans le liste getCucumberMethods().
    // Cela nécessite une initialisation complexe

}
