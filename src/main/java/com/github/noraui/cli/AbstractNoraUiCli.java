/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNoraUiCli {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNoraUiCli.class);
    protected static final String RESOURCES = "resources";

    protected String getJavaClassHeaders(String noraRobotName) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " generated free by NoraUi Organization https://github.com/NoraUi").append(System.lineSeparator());
        sb.append(" * " + noraRobotName + " is licensed under the license BSD.").append(System.lineSeparator());
        sb.append(" * CAUTION: " + noraRobotName + " use NoraUi library. This project is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE").append(System.lineSeparator());
        sb.append(" * ").append(System.lineSeparator());
        sb.append(" * @author Nicolas HALLOUIN").append(System.lineSeparator());
        sb.append(" * @author Stéphane GRILLON").append(System.lineSeparator());
        sb.append(" */").append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * @param propertiesfilePath
     *            path of properties file.	 
     * @param sb
     *            is stringBuilder contain all data.	 
     */
    protected void updateFile(String propertiesfilePath, StringBuilder sb) {
        try (FileWriter fw = new FileWriter(propertiesfilePath)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

}
