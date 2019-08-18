/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.monte.media.Format;
import org.monte.media.Registry;
import org.monte.screenrecorder.ScreenRecorder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoraUiScreenRecorder extends ScreenRecorder {

    private String name;

    public NoraUiScreenRecorder(NoraUiScreenRecorderConfiguration config, String name) throws IOException, AWTException {
        super(config.cfg, config.area, config.fileFormat, config.screenFormat, config.mouseFormat, config.audioFormat, config.movieFolder);
        this.name = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        if (!movieFolder.exists()) {
            movieFolder.mkdirs();
        } else if (!movieFolder.isDirectory()) {
            log.error("\"{}\" is not a directory.", movieFolder);
            throw new IOException("\"" + movieFolder + "\" is not a directory.");
        }
        return new File(movieFolder, name + "-" + new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date()) + "." + Registry.getInstance().getExtension(fileFormat));
    }

    public static class NoraUiScreenRecorderConfiguration {
        public GraphicsConfiguration cfg;
        public Rectangle area;
        public Format fileFormat;
        public Format screenFormat;
        public Format mouseFormat;
        public Format audioFormat;
        public File movieFolder;
    }
}