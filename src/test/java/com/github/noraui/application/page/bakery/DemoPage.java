/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page.bakery;

import static com.github.noraui.utils.Context.BAKERY_DEMO;
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
public class DemoPage extends Page {

    static Logger log;

    public final PageElement bigTitle = new PageElement("-big_title",
            "This is a demo for NORAUI (Non-Regression Automation for User Interfaces).");
    public final PageElement inputText = new PageElement("-input_text_field", "Input Text field");
    public final PageElement inputSelect = new PageElement("-input_select_field", "Input Select field");
    public final PageElement rateRadio = new PageElement("-rate", "Input radio rate");
    public final PageElement agreeCheckbox = new PageElement("-agree", "Input checkbox agree");
    public final PageElement submit = new PageElement("-submit", "Submit button");
    public final PageElement smilejs = new PageElement("-smilejs", "link a html balise with onclick by js (smilejs)");
    public final PageElement smile = new PageElement("-smile", "link a html balise with onclick (smile)");
    public final PageElement noExistElement = new PageElement("-noExistElement", "no exist element");
    public final PageElement message = new PageElement("-message", "message");
    public final PageElement xpathContainPercentChar = new PageElement("-xpathContainPercentChar");
    public final PageElement changeValueButton = new PageElement("-changeValueButton");
    public final PageElement disappearButton = new PageElement("-disappearButton");
    public final PageElement staleButton = new PageElement("-staleButton");

    private static final String TITLE_PAGE = "NoraUi Demo";

    public DemoPage() {
        super();
        this.application = BAKERY_KEY;
        this.pageKey = BAKERY_DEMO;
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_BAKERY_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("")));
            if (!TITLE_PAGE.equals(getDriver().getTitle())) {
                log.error("HTML title is not good");
                return false;
            }
        } catch (final Exception e) {
            log.error("HTML title Exception", e);
            return false;
        }
        try {
            if (bigTitle.getLabel().equals(Utilities.findElement(bigTitle).getText())) {
                return true;
            }
            log.error("Big title is not good");
            return false;
        } catch (final Exception e) {
            log.error("Big title Exception", e);
            return false;
        }
    }

}
