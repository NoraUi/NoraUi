/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import static com.github.noraui.utils.Constants.DOWNLOADED_FILES_FOLDER;
import static com.github.noraui.utils.Constants.USER_DIR;
import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.service.ScreenService;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.NoraUiScreenRecorder;
import com.google.inject.Singleton;

import io.cucumber.core.api.Scenario;

@Singleton
public class ScreenServiceImpl implements ScreenService {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenServiceImpl.class);

    private ScreenRecorder screenRecorder;

    /**
     * {@inheritDoc}
     */
    @Override
    public void takeScreenshot(Scenario scenario) {
        LOGGER.debug("takeScreenshot with the scenario named [{}]", scenario.getName());
        final byte[] screenshot = ((TakesScreenshot) Context.getDriver()).getScreenshotAs(OutputType.BYTES);
        scenario.embed(screenshot, "image/png");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveScreenshot(String screenName) throws IOException {
        LOGGER.debug("saveScreenshot with the scenario named [{}]", screenName);
        final byte[] screenshot = ((TakesScreenshot) Context.getDriver()).getScreenshotAs(OutputType.BYTES);
        FileUtils.forceMkdir(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));
        FileUtils.writeByteArrayToFile(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + screenName + ".jpg"), screenshot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveScreenshot(String screenName, WebElement element) throws IOException {
        LOGGER.debug("saveScreenshot with the scenario named [{}] and element [{}]", screenName, element.getTagName());

        final byte[] screenshot = ((TakesScreenshot) Context.getDriver()).getScreenshotAs(OutputType.BYTES);
        FileUtils.forceMkdir(new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER));

        InputStream in = new ByteArrayInputStream(screenshot);
        BufferedImage fullImg = ImageIO.read(in);

        // Get the location of element on the page
        Point point = element.getLocation();

        // Get width and height of the element
        int eleWidth = element.getSize().getWidth();
        int eleHeight = element.getSize().getHeight();

        // Crop the entire page screenshot to get only element screenshot
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
        ImageIO.write(convertType(eleScreenshot, BufferedImage.TYPE_3BYTE_BGR), "jpg",
                new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER + File.separator + screenName + ".jpg"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startVideoCapture(String screenName) throws IOException, AWTException {
        LOGGER.debug("startVideoCapture with the scenario named [{}]", screenName);
        File file = new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle captureSize = new Rectangle(0, 0, screenSize.width, screenSize.height);
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        this.screenRecorder = new NoraUiScreenRecorder(gc, captureSize, new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)), null, file, screenName);
        this.screenRecorder.start();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopVideoCapture() throws IOException {
        LOGGER.debug("stopVideoCapture");
        this.screenRecorder.stop();
    }

    private BufferedImage convertType(BufferedImage eleScreenshot, int type) {
        BufferedImage bi = new BufferedImage(eleScreenshot.getWidth(), eleScreenshot.getHeight(), type);
        Graphics g = bi.getGraphics();
        g.drawImage(eleScreenshot, 0, 0, null);
        g.dispose();
        return bi;
    }

}
