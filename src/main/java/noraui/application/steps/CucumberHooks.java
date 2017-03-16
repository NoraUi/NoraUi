package noraui.application.steps;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import noraui.browser.Auth;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.utils.Constants;
import noraui.utils.Context;
import noraui.utils.Messages;

public class CucumberHooks {

    private static final Logger logger = Logger.getLogger(CucumberHooks.class);

    @Before()
    public static void setUpScenario(Scenario scenario) throws TechnicalException {
        logger.debug("setUpScenario " + scenario.getName() + " scenario.");
        if (Context.getCurrentScenarioData() == 0) {
            // Retrieve Excel filename to read
            String scenarioName = System.getProperty("excelfilename") != null ? System.getProperty("excelfilename") : ((String) scenario.getSourceTagNames().toArray()[0]).replaceAll("@", "");
            Context.setScenarioName(scenarioName);

            Context.getDataInputProvider().prepare(Context.getScenarioName());
            Context.getDataOutputProvider().prepare(Context.getScenarioName());
            Context.startCurrentScenario();
        }

        // Increment current Excel file line to read
        Context.goToNextData();
        Context.emptyScenarioRegistry();
        Context.saveValue(Constants.IS_CONNECTED_REGISTRY_KEY, String.valueOf(Auth.isConnected()));

        Context.setCurrentScenario(scenario);
        new Result.Success<>(Context.getScenarioName(), Messages.SUCCESS_MESSAGE_BY_DEFAULT);
    }

    @After()
    public static void tearDown(Scenario scenario) {
        logger.debug("tearDown " + scenario.getName() + " scenario.");
        logger.debug("Context.getCurrentScenarioData()=" + Context.getCurrentScenarioData());
        logger.debug("ExcelFactory.getNbLines()=" + Context.getDataInputProvider().getNbGherkinExample());
        printProgressBuild(scenario);
        if (Context.getCurrentScenarioData() >= Context.getDataInputProvider().getNbGherkinExample()) {
            logger.debug("Go to next feature");
            Context.goToNextFeature();
        } else {
            logger.debug("No go to next feature");
        }
    }

    private static void printProgressBuild(Scenario scenario) {
        int remainingTime = getRemainingTime();
        StringBuilder star = new StringBuilder();
        StringBuilder postStar = new StringBuilder();
        for (int i = 0; i < 58 + scenario.getSourceTagNames().toString().length() + String.valueOf(Context.getCurrentScenarioData()).length()
                + String.valueOf(Context.getDataInputProvider().getNbGherkinExample()).length() + String.valueOf(Context.getNbFailure()).length() + String.valueOf(remainingTime).length(); i++) {
            star.append("*");
        }
        postStar.append("*");
        for (int i = 0; i < 56 + scenario.getSourceTagNames().toString().length() + String.valueOf(Context.getCurrentScenarioData()).length()
                + String.valueOf(Context.getDataInputProvider().getNbGherkinExample()).length() + String.valueOf(Context.getNbFailure()).length() + String.valueOf(remainingTime).length(); i++) {
            postStar.append(" ");
        }
        postStar.append("*");
        logger.info(star.toString());
        logger.info(postStar.toString());
        logger.info("*   Scenario: " + scenario.getSourceTagNames() + " étape " + Context.getCurrentScenarioData() + " sur " + Context.getDataInputProvider().getNbGherkinExample() + " avec "
                + Context.getNbFailure() + " erreur(s). Il reste " + remainingTime + "s   *");
        logger.info(postStar.toString());
        logger.info(star.toString());
    }

    protected static int getRemainingTime() {
        DateTime now = DateTime.now();
        Seconds pastTime = Seconds.secondsBetween(Context.getStartCurrentScenario(), now);
        int totalTimecalculated = pastTime.getSeconds() * Context.getDataInputProvider().getNbGherkinExample() / Context.getCurrentScenarioData();
        return totalTimecalculated - pastTime.getSeconds();
    }

}
