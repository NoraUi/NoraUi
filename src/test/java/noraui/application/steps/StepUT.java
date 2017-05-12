package noraui.application.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import noraui.application.page.Page;
import noraui.application.page.Page.PageElement;
import noraui.application.page.demo.DemoPage;
import noraui.cucumber.interceptor.ConditionedInterceptor;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinConditionedLoopedStep;
import noraui.gherkin.GherkinStepCondition;
import noraui.utils.Context;
import noraui.utils.Messages;

public class StepUT {

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
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("-input_text_field");
            String a = Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
            Assert.assertEquals("", "Message Input Text field in demo.", a);
        } catch (TechnicalException e) {
            Assert.assertFalse("Error", true);
        }
    }

    @Test
    public void testFormatMessage2() {
        try {
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("-submit");
            String a = Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
            Assert.assertEquals("", "Message Submit button in demo.", a);
        } catch (TechnicalException e) {
            Assert.assertFalse("Error", true);
        }
    }

    @Test
    public void testFormatMessageNullPageElement() {
        try {
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("fake");
            Messages.format("Message %s in %s.", pageElement, demoPage.getApplication());
        } catch (TechnicalException e) {
            Assert.assertEquals("TechnicalException found", "Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNullMessage() {
        try {
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format(null, pageElement, demoPage.getApplication());
        } catch (TechnicalException e) {
            Assert.assertEquals("TechnicalException found", "Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNotValidMessage() {
        try {
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format("Message %s in %s.%s", pageElement, demoPage.getApplication());
        } catch (TechnicalException e) {
            Assert.assertEquals("TechnicalException found", "Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", e.getMessage());
        }
    }

    @Test
    public void testFormatMessageNotValid2Message() {
        try {
            DemoPage demoPage = (DemoPage) Page.getInstance(DemoPage.class);
            PageElement pageElement = demoPage.getPageElementByKey("-input");
            Messages.format("Message %s.", pageElement, demoPage.getApplication());
        } catch (TechnicalException e) {
            Assert.assertEquals("TechnicalException found", "Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", e.getMessage());
        }
    }

    @Test
    public void testFindOptionByIgnoreCaseText() {
        final WebElement peterOption = mockOption("Peter");
        final WebElement stephaneOption = mockOption("Stephane");
        final WebElement noelOption = mockOption("Noël");
        final WebElement katyOption = mockOption("Céline");
        final WebElement bradOption = mockOption("Brad ");
        final WebElement pierreOption = mockOption(" Pierre");
        final WebElement christopheOption = mockOption("christophe");
        final List<WebElement> options = Arrays.asList(peterOption, stephaneOption, noelOption, katyOption, bradOption, pierreOption, christopheOption);

        final WebElement element = Mockito.mock(WebElement.class);
        Mockito.when(element.getTagName()).thenReturn("select");
        Mockito.when(element.findElements(By.tagName("option"))).thenReturn(options);
        Select select = new Select(element);

        Assert.assertEquals("KO", -1, step.findOptionByIgnoreCaseText("fake", select));

        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText("Peter", select));
        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText(" Peter", select));
        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText("Peter ", select));
        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText("peter", select));
        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText(" peter", select));
        Assert.assertEquals("OK", 0, step.findOptionByIgnoreCaseText("peter ", select));

        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText("Stephane", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText(" Stephane", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText("Stephane ", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText(" Stephane ", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText("Stéphane", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText(" Stéphane", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText("Stéphane ", select));
        Assert.assertEquals("OK", 1, step.findOptionByIgnoreCaseText(" Stéphane ", select));

        Assert.assertEquals("OK", 2, step.findOptionByIgnoreCaseText("Noël", select));
        Assert.assertEquals("OK", 2, step.findOptionByIgnoreCaseText("Noel", select));

        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText("Celine", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText(" Celine", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText("Celine ", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText(" Celine ", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText("Céline", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText(" Céline", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText("Céline ", select));
        Assert.assertEquals("OK", 3, step.findOptionByIgnoreCaseText(" Céline ", select));

        Assert.assertEquals("OK", 4, step.findOptionByIgnoreCaseText("Brad", select));
        Assert.assertEquals("OK", 4, step.findOptionByIgnoreCaseText(" Brad", select));
        Assert.assertEquals("OK", 4, step.findOptionByIgnoreCaseText("Brad ", select));
        Assert.assertEquals("OK", 4, step.findOptionByIgnoreCaseText(" Brad ", select));

        Assert.assertEquals("OK", 5, step.findOptionByIgnoreCaseText("Pierre", select));
        Assert.assertEquals("OK", 5, step.findOptionByIgnoreCaseText(" Pierre", select));
        Assert.assertEquals("OK", 5, step.findOptionByIgnoreCaseText("Pierre ", select));
        Assert.assertEquals("OK", 5, step.findOptionByIgnoreCaseText(" Pierre ", select));

        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText("christophe", select));
        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText(" christophe", select));
        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText("christophe ", select));
        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText("Christophe", select));
        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText(" Christophe", select));
        Assert.assertEquals("OK", 6, step.findOptionByIgnoreCaseText("Christophe ", select));
    }

    @Test
    public void testRunAllStepsInLoop() {
        List<GherkinConditionedLoopedStep> steps = new ArrayList<>();
        String expected = ".+;(ETS Backbone VLAN LL2048K\\|ETS Accès L2ETH\\|Accès XDSL ETS\\|Backbone VLAN Virtuelle)";
        String actual = "VPNtechnique;OSC_ACC-resource_type";
        Context.saveValue("OSC_ACC-resource_type", "ETS Accès L2ETH");
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        steps.add(gherkinConditionedLoopedStep);
        try {
            step.runAllStepsInLoop(steps);
        } catch (TechnicalException e) {
            Assert.assertFalse("Error", true);
        }
    }

    private WebElement mockOption(String name) {
        final WebElement option = Mockito.mock(WebElement.class, name);
        Mockito.when(option.getText()).thenReturn(name);
        return option;
    }

}
