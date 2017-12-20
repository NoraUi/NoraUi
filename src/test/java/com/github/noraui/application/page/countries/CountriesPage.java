/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.countries;

import static com.github.noraui.utils.Context.COUNTRIES_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;
import com.google.inject.Singleton;

@Singleton
public class CountriesPage extends Page {

    /**
     * Specific logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(CountriesPage.class);

    public final PageElement login = new PageElement("-login_field", "Login");
    public final PageElement loginTitle = new PageElement("-login_title", "Login title");
    public final PageElement password = new PageElement("-password_field", "Password");
    public final PageElement signInButton = new PageElement("-sign_in_button", "Sign-in button");

    public CountriesPage() {
        super();
        this.application = COUNTRIES_KEY;
        this.pageKey = "COUNTRIES_HOM";
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_COUNTRIES_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(loginTitle)));
            return true;
        } catch (Exception e) {
            logger.error("login title not found", e);
            return false;
        }
    }

}