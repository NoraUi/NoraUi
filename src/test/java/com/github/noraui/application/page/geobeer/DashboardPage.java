/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.geobeer;

import static com.github.noraui.utils.Context.GEOBEER_KEY;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.application.page.Page;
import com.github.noraui.exception.Callbacks;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Utilities;

public class DashboardPage extends Page {

    /**
     * Specific logger
     */
    protected static final Logger logger = LoggerFactory.getLogger(DashboardPage.class);

    public final PageElement signInMessage = new PageElement("-sign_in_message");
    public final PageElement signoutMenu = new PageElement("-signoutMenu", "Sign-out menu");

    public DashboardPage() {
        super();
        this.application = GEOBEER_KEY;
        this.pageKey = "GEOBEER_DAS";
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_GEOBEER_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(signInMessage)));
            return true;
        } catch (Exception e) {
            logger.error("signIn message not found", e);
            return false;
        }
    }

}
