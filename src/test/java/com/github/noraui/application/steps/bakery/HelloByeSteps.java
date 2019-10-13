/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.bakery;

import static com.github.noraui.utils.Constants.DOWNLOADED_FILES_FOLDER;
import static com.github.noraui.utils.Constants.USER_DIR;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.model.demo.Article;
import com.github.noraui.application.model.demo.Articles;
import com.github.noraui.application.page.bakery.DemoPage;
import com.github.noraui.application.steps.ExpectSteps;
import com.github.noraui.application.steps.Step;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulator;
import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulators;
import com.github.noraui.cucumber.metrics.annotation.time.Time;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Lorsque;

public class HelloByeSteps extends Step {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloByeSteps.class);

    @Inject
    private DemoPage demoPage;

    @Alors("Le portail DEMO est affiché")
    @Then("The DEMO portal is displayed")
    public void checkDemoPortalPage() throws FailureException {
        if (!demoPage.checkPage()) {
            new Result.Failure<>("DEMO", Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, this.demoPage.getCallBack());
        }
    }

    @Lorsque("Je fait la créaton du fichier test.txt dans repertoire des téléchargements")
    @Given("I create test.txt file in download directory")
    public void createTestTxtFileInDownloadDirectory() throws IOException {
        File f = new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + "test.txt");
        if (!f.exists()) {
            f.createNewFile();
        } else {
            LOGGER.warn("File already exists");
        }
    }

    @Time
    @SpeedRegulators({ @SpeedRegulator(application = "DEMO2", costString = "${demo2.cost}", verbose = true), @SpeedRegulator(application = "DEMO3", cost = 2, unit = TimeUnit.SECONDS) })
    @Conditioned
    @Etantdonné("j'ai un bonjour, s'il vous plaît. Cordialement {string}(\\?)")
    @Given("me a hello, please. Best Regards {string}(\\?)")
    public void hello(String name, List<GherkinStepCondition> conditions) {
        LOGGER.info("Hello " + name + "!");
    }

    @Lorsque("moi un chat, s'il vous plaît, meilleures salutations {string}.")
    @Given("me a cat, please. Best Regards {string}.")
    public void hello(String name) {
        LOGGER.info("Take my cat " + name + "!");
    }

    @Lorsque("j'ai un au revoir, s'il vous plaît. Cordialement {string}:")
    @Given("me a bye, please. Best Regards {string}:")
    public void bye(String name, Map<String, String> params) {
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            i++;
            LOGGER.info(String.format("  Key N°%d: %s   Value:%s", i, entry.getKey(), entry.getValue()));
        }
        LOGGER.info("Bye " + name + "!");
    }

    @Lorsque("moi une erreur si {string} est Paris.")
    @Given("me a error if {string} is Paris.")
    public void error(String city) throws FailureException {
        if ("Paris".equals(city)) {
            new Result.Failure<>(city, "The city is Paris!!", true, this.demoPage.getCallBack());
        }
    }

    @Lorsque("Mes champs sont prêts à être utilisés")
    @Given("My fields are ready to use")
    public void checkFields() throws FailureException {
        By inputSelectLocator = Utilities.getLocator(demoPage.inputSelect);
        By inputTextLocator = Utilities.getLocator(demoPage.inputText);
        Context.waitUntil(ExpectSteps.atLeastOneOfTheseElementsIsPresent(inputSelectLocator, inputTextLocator));
        Context.waitUntil(ExpectSteps.presenceOfNbElementsLocatedBy(inputSelectLocator, 1));
        Context.waitUntil(ExpectSteps.presenceOfNbElementsLocatedBy(inputTextLocator, 1));
        Context.waitUntil(ExpectSteps.visibilityOfNbElementsLocatedBy(inputSelectLocator, 1));
        Context.waitUntil(ExpectSteps.visibilityOfNbElementsLocatedBy(inputTextLocator, 1));
    }

    @RetryOnFailure(attempts = 3)
    @Given("me any article, please. {string} of {string}.")
    public void readBlog(String jsonArticles, String blog) throws FailureException {
        Articles articles = new Articles();
        articles.deserialize(jsonArticles);
        for (Article article : articles) {
            if ("anonymous".equals(article.getAuthor())) {
                new Result.Failure<>("anonymous", "anonymous is prohibited in demo blog!!", true, this.demoPage.getCallBack());
            } else {
                LOGGER.info("> " + blog);
                LOGGER.info("    > " + article.getTitle() + ": " + article.getText());
            }
        }
    }

    @Lorsque("test pour {string}")
    @Given("test for {string}")
    public void testforCancel(String word) {
        LOGGER.info("testforCancel: " + word);
    }

}
