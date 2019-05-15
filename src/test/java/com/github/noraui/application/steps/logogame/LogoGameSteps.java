/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.logogame;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.business.logogame.ProhibitedBrands;
import com.github.noraui.application.model.logogame.Logo;
import com.github.noraui.application.model.logogame.Logos;
import com.github.noraui.application.page.logogame.LogoGamePage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.fr.Alors;
import cucumber.api.java.fr.Et;
import cucumber.api.java.fr.Lorsque;
import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeValue;

public class LogoGameSteps extends Step {

    /**
     * Specific logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoGameSteps.class);

    @Inject
    private LogoGamePage logoGamePage;

    @Alors("Le portail LOGOGAME est affiché")
    @Then("The LOGOGAME portal is displayed")
    public void checkDemoPortalPage() throws FailureException {
        if (!this.logoGamePage.checkPage()) {
            new Result.Failure<>("LOGOGAME", Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, this.logoGamePage.getCallBack());
        }
    }

    @Time
    @Alors("Je joue avec 'amazon'")
    @Then("I play with 'amazon'")
    public void playAmazon() throws FailureException {
        try {
            updateText(this.logoGamePage.amazonElement, "amazon");
        } catch (TechnicalException e) {
            new Result.Failure<>("LOGOGAME", Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), true, this.logoGamePage.getCallBack());
        }
    }

    @Time()
    @Alors("J'ajoute {int} marque(s) aléatoire(s)")
    @Then("I add {int} random brand(s)")
    public void addRandomBrand(@TimeValue("nb") int nb) throws FailureException {
        for (int i = 0; i < nb; i++) {
            try {
                clickOn(this.logoGamePage.addButton);
            } catch (TechnicalException e) {
                new Result.Failure<>("LOGOGAME", Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), true, this.logoGamePage.getCallBack());
            }
        }
    }

    @Et("Je vérifie le message d'alerte")
    @And("I check alert message")
    public void checkAlertMessage() throws TechnicalException, FailureException {
        checkText(this.logoGamePage.alertMessage, "There are no more logos available");
    }

    /**
     * A check that all brands are not prohibited, because any minors can not play with alcohol and tobacco brands.
     *
     * @param jsonLogos
     *            Serialized Json representation of all logos (all brands)
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    @Lorsque("Je vérifie que toutes les marques {string} ne sont pas interdites")
    @Given("I check that all brands {string} are not prohibited")
    public void checkThatAllBrandsIsNotProhibited(String jsonLogos) throws TechnicalException, FailureException {
        Logos logos = new Logos();
        logos.deserialize(jsonLogos);
        for (int i = 0; i < logos.size(); i++) {
            if (ProhibitedBrands.getAlcool().contains(logos.get(i).getBrand()) || ProhibitedBrands.getTabaco().contains(logos.get(i).getBrand())) {
                new Result.Failure<>(logos.get(i).getBrand(), Messages.format("Brand « %s » is prohibited.", logos.get(i).getBrand()), false, logos.get(i).getNid(),
                        Context.getCallBack(Callbacks.RESTART_WEB_DRIVER));
            }
        }
    }

    @Alors("Je joue avec mon fichier d'entrée {string}")
    @Then("I play with my input file {string}")
    public void playWithMyInputFile(String jsonLogos) throws TechnicalException {
        Logos logos = new Logos();
        logos.deserialize(jsonLogos);
        for (int i = 0; i < logos.size(); i++) {
            Logo logo = logos.get(i);
            logo.setNid(i);
            try {
                WebElement element = getDriver().findElement(Utilities.getLocator(this.logoGamePage.brandElement, logo.getBrand(), logo.getBrand()));
                if (element != null) {
                    updateText(this.logoGamePage.brandElement, logo.getBrand(), null, logo.getBrand(), logo.getBrand());
                }
            } catch (Exception e) {
                new Result.Warning<>(logo.getBrand(), Messages.format("Brand « %s » does not exist.", logo.getBrand()), true, logo.getNid());
            }
        }
    }

    @Alors("Je valide dans LOGOGAME")
    @Then("I valide in LOGOGAME")
    public void valid() throws FailureException {
        try {
            clickOn(this.logoGamePage.validateButton);
        } catch (TechnicalException e) {
            new Result.Failure<>("LOGOGAME", Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE) + e.getMessage(), true, this.logoGamePage.getCallBack());
        }
    }

    @Alors("Je sauvegarde le score")
    @Then("I save score")
    public void saveScore() throws FailureException {
        try {
            WebElement message = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.logoGamePage.scoreMessage)));
            try {
                Context.getCurrentScenario().write("score is:\n" + message.getText());
                Context.getDataOutputProvider().writeDataResult("score", Context.getDataInputProvider().getIndexData(Context.getCurrentScenarioData()).getIndexes().get(0), message.getText());
            } catch (TechnicalException e) {
                LOGGER.error(Messages.getMessage(TechnicalException.TECHNICAL_ERROR_MESSAGE), e);
            }
        } catch (Exception e) {
            new Result.Failure<>(e.getMessage(), "", true, this.logoGamePage.getCallBack());
        }
    }

}
