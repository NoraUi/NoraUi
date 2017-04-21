package noraui.application.page.logogame;

import static noraui.utils.Context.LOGOGAME_KEY;

import org.apache.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;

import noraui.application.page.Page;
import noraui.exception.Callbacks;
import noraui.utils.Context;
import noraui.utils.Utilities;

public class LogoGamePage extends Page {

    private static Logger logger = Logger.getLogger(LogoGamePage.class.getName());

    public final PageElement bigTitle = new PageElement("-big-title", "Logo Game");
    public final PageElement amazonElement = new PageElement("-amazonElement", "Input Text Amazon");
    public final PageElement brandElement = new PageElement("-brandElement");
    public final PageElement validateButton = new PageElement("-validateButton", "Validate Button");
    public final PageElement addButton = new PageElement("-addButton", "Add Button");
    public final PageElement brandList = new PageElement("-brandList", "Brand Drop Down");
    public final PageElement scoreMessage = new PageElement("-scoreMessage", "Score message");
    public final PageElement alertMessage = new PageElement("-alertMessage", "Alert message");

    private static final String TITLE_PAGE = "Logo Game";

    public LogoGamePage() {
        super();
        this.application = LOGOGAME_KEY;
        this.pageKey = "LOGOGAME_HOME";
        this.callBack = Context.getCallBack(Callbacks.CLOSE_WINDOW_AND_SWITCH_TO_LOGOGAME_HOME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkPage(Object... elements) {
        try {
            Context.waitUntil(ExpectedConditions.not(ExpectedConditions.titleIs("")));
            if (!TITLE_PAGE.equals(getDriver().getTitle())) {
                logger.error("HTML title is not good");
                return false;
            }
        } catch (Exception e) {
            logger.error("HTML title Exception", e);
            return false;
        }
        try {
            if (bigTitle.getLabel().equals(Utilities.findElement(bigTitle).getText())) {
                return true;
            }
            logger.error("Big title is not good");
            return false;
        } catch (Exception e) {
            logger.error("Big title Exception", e);
            return false;
        }
    }

}