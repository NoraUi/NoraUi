package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNoraUiCli {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNoraUiCli.class);
    protected static final String RESOURCES = "resources";

    protected String getJavaClassHeaders(String noraRobotName) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " generated free by NoraUi Organization https://github.com/NoraUi").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " is licensed under the license BSD.").append(System.lineSeparator());
        sb.append(" * ").append(System.lineSeparator());
        sb.append(" * CAUTION: " + noraRobotName + " use NoraUi library. This project is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE").append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * @param propertiesfilePath
     * @param sb
     */
    protected void updatePropertiesFile(String propertiesfilePath, StringBuilder sb) {
        try (FileWriter fw = new FileWriter(propertiesfilePath)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

}
