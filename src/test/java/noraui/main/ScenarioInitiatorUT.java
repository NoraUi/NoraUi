package noraui.main;

import org.junit.Assert;
import org.junit.Test;

import noraui.utils.Context;

public class ScenarioInitiatorUT {

    @Test
    public void initUnitTest() {
        Assert.assertTrue("init TU OK", true);
    }

    @Test
    public void mainExcelTest() {
        Context.getInstance().initializeEnv("demoExcel.properties");
        new ScenarioInitiator().start(null);
        Context.clear();
    }

    @Test
    public void mainExcelWithArgsTest() {
        Context.getInstance().initializeEnv("demoExcel.properties");
        String[] args = new String[1];
        args[0] = "hello";
        new ScenarioInitiator().start(args);
        Context.clear();
    }

    @Test
    public void mainExcelScenarioInitiatorRunnerTest() {
        Context.getInstance().initializeEnv("demoExcel.properties");
        ScenarioInitiatorRunner.main(null);
        Context.clear();
    }

    @Test
    public void mainGherkinTest() {
        Context.getInstance().initializeEnv("demoGherkin.properties");
        new ScenarioInitiator().start(null);
        Context.clear();
    }

    @Test
    public void mainGherkinWithArgsTest() {
        Context.getInstance().initializeEnv("demoGherkin.properties");
        String[] args = new String[1];
        args[0] = "hello";
        new ScenarioInitiator().start(args);
        Context.clear();
    }

    @Test
    public void mainGherkinScenarioInitiatorRunnerTest() {
        Context.getInstance().initializeEnv("demoGherkin.properties");
        ScenarioInitiatorRunner.main(null);
        Context.clear();
    }
}
