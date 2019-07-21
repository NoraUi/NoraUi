/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.bakery;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.bakery.AdminPage;
import com.github.noraui.application.page.bakery.BakeryPage;
import com.github.noraui.application.page.bakery.ReferencerPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.browser.Auth;
import com.github.noraui.cucumber.annotation.Conditioned;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Alors;
import cucumber.api.java.fr.Quand;

public class BakerySteps extends Step {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(BakerySteps.class);

    @Inject
    private BakeryPage bakeryPage;

    @Inject
    private AdminPage adminPage;
    
    @Inject
    private ReferencerPage referencerPage;

    /**
     * Check home page.
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Alors("La page d'accueil BAKERY est affichée")
    @Then("The BAKERY home page is displayed")
    public void checkBakeryLoginPage() throws FailureException {
        if (!bakeryPage.checkPage()) {
            new Result.Failure<>(bakeryPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_HOME_PAGE_NOT_FOUND), true, bakeryPage.getCallBack());
        }
    }

    /**
     * Log in to BAKERY.
     *
     * @param login
     *            Login to use.
     * @param password
     *            Password to use.
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Alors("Je me connect sur BAKERY avec '(.*)' '(.*)'")
    @Then("I log in to BAKERY as '(.*)' '(.*)'")
    public void logInToBakery(String login, String password) throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(bakeryPage.signInButton)));
            Utilities.findElement(bakeryPage.login).sendKeys(login);
            Utilities.findElement(bakeryPage.password).sendKeys(cryptoService.decrypt(password));
            Utilities.findElement(bakeryPage.signInButton).click();
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, bakeryPage.getCallBack());
        }
    }

    @Conditioned
    @Alors("La partie administrateur du portail BAKERY est affichée(\\?)")
    @Then("The administrator part of the BAKERY portal is displayed(\\?)")
    public void checkAdministratorPage(List<GherkinStepCondition> conditions) throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(adminPage.titleMessage)));
            if (!adminPage.checkPage()) {
                logInToBakeryWithBakeryRobot();
            }
            if (!adminPage.checkPage()) {
                new Result.Failure<>(adminPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, adminPage.getCallBack());
            }
        } catch (Exception e) {
            new Result.Failure<>(adminPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, adminPage.getCallBack());
        }
        Auth.setConnected(true);
    }

    @Conditioned
    @Alors("La partie referenceur du portail BAKERY est affichée(\\?)")
    @Then("The referencer part of the BAKERY portal is displayed(\\?)")
    public void checkReferencerPage(List<GherkinStepCondition> conditions) throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(referencerPage.titleMessage)));
            if (!referencerPage.checkPage()) {
                logInToBakeryWithBakeryRobot();
            }
            if (!referencerPage.checkPage()) {
                new Result.Failure<>(referencerPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, referencerPage.getCallBack());
            }
        } catch (Exception e) {
            new Result.Failure<>(referencerPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, referencerPage.getCallBack());
        }
        Auth.setConnected(true);
    }
    
    /**
     * Logout of Bakery.
     * 
     * @throws FailureException
     *             if the scenario encounters a functional error.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @Quand("Je me déconnecte de BAKERY")
    @When("I log out of BAKERY")
    public void logOut() throws FailureException, TechnicalException {
        if (Auth.isConnected()) {
            getDriver().switchTo().defaultContent();
            clickOn(adminPage.accountMenu);
            WebElement outMenu = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.adminPage.signOutMenu)));
            outMenu.click();
        } else {
            // TODO
            //Context.getCurrentScenario().write(Messages.getMessage("..."));
            Context.getCurrentScenario().write("not connected");
        }
    }

    /**
     * Check Logout page.
     * 
     * @throws FailureException
     */
    @Alors("La page de déconnexion de BAKERY est affichée")
    @Then("The BAKERY logout page is displayed")
    public void checkBakeryLogoutPage() throws FailureException {
        if (!bakeryPage.checkPage()) {
            new Result.Failure<>(bakeryPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_LOGOUT), true, bakeryPage.getCallBack());
        }
    }

    /**
     * Log in to BAKERY with BakeryRobot (login and password in job parameters).
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    private void logInToBakeryWithBakeryRobot() throws FailureException {
        String login = Auth.getLogin();
        String password = Auth.getPassword();
        if (!"".equals(login) && !"".equals(password)) {
            logInToBakery(login, password);
        }
    }

}
