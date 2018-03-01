/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.gherkin;

import org.junit.Assert;
import org.junit.Test;

public class GherkinConditionedLoopedStepUT {

    public static final String DO_NOT_MATCH_STRING = "^(?!.*IPADSL|.*C2E|.*LL2048).*$";
    public static final String CONTAINS_STRING = "^(.*Accès L2ETH|.*Accès L2TP).*$";

    @Test
    public void testCheckConditionsEquals() {
        String expected = "123";
        String actual = "123";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertTrue(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsFalseEquals() {
        String expected = "yes";
        String actual = "no";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertFalse(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsEqualsIgnoreCase() {
        String expected = "abc";
        String actual = "Abc";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertTrue(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsNotNull() {
        String expected = "^$";
        String actual = "";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertTrue(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsFalseNotNull() {
        String expected = "^$";
        String actual = "stub";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertFalse(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsNotEmpty() {
        String expected = ".+";
        String actual = "stub";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertTrue(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsFalseNotEmpty() {
        String expected = ".+";
        String actual = "";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertFalse(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsDoNotMatchString() {
        String expected = DO_NOT_MATCH_STRING;
        String actual = "stub";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertTrue(gherkinConditionedLoopedStep.checkCondition());
    }

    @Test
    public void testCheckConditionsFalseDoNotMatchStringIPADSL() {
        String expected = DO_NOT_MATCH_STRING;
        String actual = "stub IPADSL";
        GherkinConditionedLoopedStep gherkinConditionedLoopedStep = new GherkinConditionedLoopedStep("1", "I wait '4' seconds.", expected, actual);
        Assert.assertFalse(gherkinConditionedLoopedStep.checkCondition());
    }

}
