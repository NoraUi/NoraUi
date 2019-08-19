/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import java.awt.AWTException;
import java.io.IOException;

import org.openqa.selenium.WebElement;

import io.cucumber.core.api.Scenario;

public interface ScreenService {

    /**
     * Indicates a driver that can capture a screenshot and store it in different ways.
     *
     * @param scenario
     *            is instance of {link io.cucumber.core.api.Scenario}
     */
    void takeScreenshot(Scenario scenario);

    /**
     * Indicates a driver that can capture a screenshot and store it in different ways.
     * 
     * @param screenName
     *            name of screenshot file.
     * @throws IOException
     *             if file or directory is wrong.
     */
    void saveScreenshot(String screenName) throws IOException;

    /**
     * Indicates a driver that can capture a screenshot of one element only and store it in different ways.
     * 
     * @param screenName
     *            name of screenshot file.
     * @param element
     *            WebElement concerned
     * @throws IOException
     *             if file or directory is wrong.
     */
    void saveScreenshot(String screenName, WebElement element) throws IOException;

    /**
     * Start video capture with screenName.avi in DOWNLOAD_FILES_FOLDER folder.
     * 
     * @param screenName
     *            name of output file (video file).
     * @throws IOException
     *             if file or directory is wrong.
     * @throws AWTException
     *             if configuration video file is wrong.
     */
    void startVideoCapture(String screenName) throws IOException, AWTException;

    /**
     * Stop video capture in DOWNLOAD_FILES_FOLDER folder..
     * 
     * @throws IOException
     *             if file or directory is wrong.
     */
    void stopVideoCapture() throws IOException;

}
