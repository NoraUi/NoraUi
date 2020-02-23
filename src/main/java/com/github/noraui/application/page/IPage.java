/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.application.page;

import org.openqa.selenium.WebDriver;

import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.exception.Callbacks.Callback;

public interface IPage {

    /**
     * Quick getter to the global web driver
     * 
     * @return
     *         The global instance of web driver
     */
    WebDriver getDriver();

    /**
     * @return noraui.exception.Callbacks.Callback of page.
     */
    Callback getCallBack();

    /**
     * @param key
     *            of PageElement
     * @return noraui.application.page.Page.PageElement
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    PageElement getPageElementByKey(String key) throws IllegalArgumentException, IllegalAccessException;

    /**
     * @return key of page
     */
    String getPageKey();

    /**
     * @return application name
     */
    String getApplication();

    /**
     * @return get the linked container page.
     */
    Page getMotherPage();

    /**
     * set the linked container page.
     *
     * @param motherPage
     *            is the mother page linked
     */
    void setMotherPage(Page motherPage);

    /**
     * @param elements
     *            in a array of element
     * @return true or false
     */
    boolean checkPage(Object... elements);

    interface IPageElement {

        /**
         * @return key of page element (The key that is used to find the right selector in the ini file).
         */
        String getKey();

        /**
         * @return label of page element (The wording that is displayed in the report).
         */
        String getLabel();

        /**
         * @return page of page element
         */
        IPage getPage();

    }
}
