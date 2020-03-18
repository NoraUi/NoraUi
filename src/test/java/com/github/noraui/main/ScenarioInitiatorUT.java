/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.main;

import org.junit.Assert;
import org.junit.Test;

import com.github.noraui.utils.Context;

public class ScenarioInitiatorUT {

    @Test
    public void initUnitTest() {
        Assert.assertTrue("init TU OK", true);
    }

    @Test
    public void mainExcelTest() {
        testScenarioInitiatorStart("demoExcel.properties", null);
    }

    @Test
    public void mainExcelWithArgsTest() {
        String[] args = { "hello" };
        testScenarioInitiatorStart("demoExcel.properties", args);
    }

    @Test
    public void mainExcelScenarioInitiatorRunnerTest() {
        testScenarioInitiatorRunner("demoExcel.properties", null);
    }

    @Test
    public void mainGherkinTest() {
        testScenarioInitiatorStart("demoGherkin.properties", null);
    }

    @Test
    public void mainGherkinWithArgsTest() {
        String[] args = { "hello" };
        testScenarioInitiatorStart("demoGherkin.properties", args);
    }

    @Test
    public void mainGherkinScenarioInitiatorRunnerTest() {
        testScenarioInitiatorRunner("demoGherkin.properties", null);
    }

    private void testScenarioInitiatorStart(String prop, String[] args) {
        String res = "";
        try {
            Context.getInstance().initializeEnv(prop);
            new ScenarioInitiator().start(args);
            Context.quit();
        } catch (Exception e) {
            res = "An error occured during ScenarioInitiator.main(\"" + args + "\") with \"" + prop + "\": " + e.getMessage();
        }
        Assert.assertEquals(res, "", res);
    }

    private void testScenarioInitiatorRunner(String prop, String[] args) {
        String res = "";
        try {
            Context.getInstance().initializeEnv(prop);
            ScenarioInitiatorRunner.main(args);
            Context.quit();
        } catch (Exception e) {
            res = "An error occured during ScenarioInitiator.main(\"" + args + "\") with \"" + prop + "\": " + e.getMessage();
        }
        Assert.assertEquals(res, "", res);
    }
}
