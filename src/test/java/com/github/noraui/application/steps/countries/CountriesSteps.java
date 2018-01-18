/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.steps.countries;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.countries.CountriesPage;
import com.github.noraui.application.page.countries.DashboardPage;
import com.github.noraui.application.page.countries.LogoutPage;
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

public class CountriesSteps extends Step {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CountriesSteps.class);

    @Inject
    private CountriesPage countriesPage;

    @Inject
    private DashboardPage dashboardPage;

    @Inject
    private LogoutPage logoutPage;

    /**
     * Check Login page.
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Then("The COUNTRIES home page is displayed")
    public void checkCountriesLoginPage() throws FailureException {
        if (!countriesPage.checkPage()) {
            new Result.Failure<>(countriesPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, countriesPage.getCallBack());
        }
    }

    /**
     * Log in to COUNTRIES.
     *
     * @param login
     *            Login to use.
     * @param password
     *            Password to use.
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    @Alors("Je me connect sur COUNTRIES avec '(.*)' '(.*)'")
    @Then("I log in to COUNTRIES as '(.*)' '(.*)'")
    public void logInToCountries(String login, String password) throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(countriesPage.signInButton)));
            Utilities.findElement(countriesPage.login).sendKeys(login);
            Utilities.findElement(countriesPage.password).sendKeys(password);
            Utilities.findElement(countriesPage.signInButton).click();
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.getMessage(Messages.FAIL_MESSAGE_UNKNOWN_CREDENTIALS), true, countriesPage.getCallBack());
        }
    }

    @Alors("Le portail COUNTRIES est affiché")
    @Then("The COUNTRIES portal is displayed")
    public void checkDemoPortalPage() throws FailureException {
        try {
            Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(dashboardPage.signInMessage)));
            if (!dashboardPage.checkPage()) {
                logInToCountriesWithCountriesRobot();
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
     * Logout of Countries.
     * 
     * @throws FailureException
     *             if the scenario encounters a functional error.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    @When("I log out of COUNTRIES")
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
    @Then("The COUNTRIES logout page is displayed")
    public void checkCountriesLogoutPage() throws FailureException {
        if (!logoutPage.checkPage()) {
            new Result.Failure<>(logoutPage.getApplication(), Messages.getMessage(Messages.FAIL_MESSAGE_LOGOUT), true, logoutPage.getCallBack());
        }
    }

    /**
     * Log in to COUNTRIES with CountriesRobot (login and password in job parameters).
     *
     * @throws FailureException
     *             if the scenario encounters a functional error.
     */
    private void logInToCountriesWithCountriesRobot() throws FailureException {
        String login = Auth.getLogin();
        String password = Auth.getPassword();
        if (!"".equals(login) && !"".equals(password)) {
            logInToCountries(login, password);
        }
    }

}
