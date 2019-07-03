/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.geobeer;

import static com.github.noraui.utils.Context.GEOBEER_HOME;
import static com.github.noraui.utils.Context.GEOBEER_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;
import com.google.inject.Singleton;

@Singleton
public class GeobeerPage extends Page {

    /**
     * Specific logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(GeobeerPage.class);

    public final PageElement login = new PageElement("-login_field", "Login");
    public final PageElement loginTitle = new PageElement("-login_title", "Login title");
    public final PageElement password = new PageElement("-password_field", "Password");
    public final PageElement signInButton = new PageElement("-sign_in_button", "Sign-in button");

    public GeobeerPage() {
        super();
        this.application = GEOBEER_KEY;
        this.pageKey = GEOBEER_HOME;
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME);
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