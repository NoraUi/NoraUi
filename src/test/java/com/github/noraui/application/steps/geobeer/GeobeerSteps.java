/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.geobeer;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.geobeer.DashboardPage;
import com.github.noraui.application.page.geobeer.GeobeerPage;
import com.github.noraui.application.page.geobeer.LogoutPage;
import com.github.noraui.application.steps.Step;
import com.github.noraui.browser.Auth;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.Result;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;
import com.github.noraui.utils.Utilities;
import com.google.inject.Inject;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.fr.Alors;

public class GeobeerSteps extends Step {

    /**
     * Specific logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeobeerSteps.class);

    @Inject
    private GeobeerPage geobeerPage;

    @Inject
    private DashboardPage dashboardPage;

    @Inject
    private LogoutPage logoutPage;

    /**
     * Check home page.
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Then("The GEOBEER home page is displayed")
    public void checkGeobeerLoginPage() throws FailureException {
        if (!geobeerPage.checkPage()) {
            new Result.Failure<>(geobeerPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_HOME_PAGE_NOT_FOUND), true, geobeerPage.getCallBack());
        }
    }

    /**
     * Log in to GEOBEER.
     *
     * @param login
     *            Login to use.
     * @param password
     *            Password to use.
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Alors("Je me connect sur GEOBEER avec '(.*)' '(.*)'")
    @Then("I log in to GEOBEER as '(.*)' '(.*)'")
    public void logInToGeobeer(String login, String password) throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(geobeerPage.signInButton)));
            Utilities.findElement(geobeerPage.login).sendKeys(login);
            Utilities.findElement(geobeerPage.password).sendKeys(password);
            Utilities.findElement(geobeerPage.signInButton).click();
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, geobeerPage.getCallBack());
        }
    }

    @Alors("Le portail GEOBEER est affiché")
    @Then("The GEOBEER portal is displayed")
    public void checkDemoPortalPage() throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(dashboardPage.signInMessage)));
            if (!dashboardPage.checkPage()) {
                logInToGeobeerWithGeobeerRobot();
            }
            if (!dashboardPage.checkPage()) {
                new Result.Failure<>(dashboardPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, dashboardPage.getCallBack());
            }
        } catch (Exception e) {
            new Result.Failure<>(dashboardPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, dashboardPage.getCallBack());
        }
        Auth.setConnected(true);
    }

    /**
     * Logout of Geobeer.
     * 
     * @throws FailureException
     *             if the scenario encounters a functional error.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @When("I log out of GEOBEER")
    public void logOut() throws FailureException, TechnicalException {
        if (Auth.isConnected()) {
            getDriver().switchTo().defaultContent();
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(this.dashboardPage.signoutMenu))).click();
        }
    }

    /**
     * Check Logout page.
     * 
     * @throws FailureException
     */
    @Then("The GEOBEER logout page is displayed")
    public void checkGeobeerLogoutPage() throws FailureException {
        if (!logoutPage.checkPage()) {
            new Result.Failure<>(logoutPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_LOGOUT), true, logoutPage.getCallBack());
        }
    }

    /**
     * Log in to GEOBEER with GeobeerRobot (login and password in job parameters).
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    private void logInToGeobeerWithGeobeerRobot() throws FailureException {
        String login = Auth.getLogin();
        String password = Auth.getPassword();
        if (!"".equals(login) && !"".equals(password)) {
            logInToGeobeer(login, password);
        }
    }

}
