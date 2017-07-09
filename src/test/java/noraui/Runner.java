package noraui;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import noraui.utils.Context;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = true, glue = { "noraui.application.steps", "noraui.browser.steps" }, plugin = { "html:target/reports/html" }, features = { "src/test/resources" })
public class Runner {

    /**
     * BeforeClass Read constants file
     */
    @BeforeClass
    public static void setUpClass() {
        Context.getInstance().initializeEnv("demoGherkin.properties");
        Context.getInstance().initializeRobot(Runner.class);
    }

    /**
     * AfterClass Read constants file
     */
    @AfterClass
    public static void tearDownClass() {
        Context.clear();
    }

}
