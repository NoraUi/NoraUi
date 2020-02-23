/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.bakery;

import static com.github.noraui.utils.Context.BAKERY_HOME;
import static com.github.noraui.utils.Context.BAKERY_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;

import com.github.noraui.application.page.Page;
import com.github.noraui.browser.waits.Wait;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;
import com.google.inject.Singleton;

@Loggable
@Singleton
public class BakeryPage extends Page {

    static Logger log;

    public final PageElement login = new PageElement("-login_field", "Login");
    public final PageElement loginTitle = new PageElement("-login_title", "Login title");
    public final PageElement password = new PageElement("-password_field", "Password");
    public final PageElement signInButton = new PageElement("-sign_in_button", "Sign-in button");
    public final PageElement signOutMessage = new PageElement("-sign_out_message");

    public BakeryPage() {
        super();
        this.application = BAKERY_KEY;
        this.pageKey = BAKERY_HOME;
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Wait.until(ExpectedConditions.visibilityOfElementLocated(Utilities.getLocator(loginTitle)));
            return true;
        } catch (Exception e) {
            log.error("login title not found", e);
            return false;
        }
    }

}
