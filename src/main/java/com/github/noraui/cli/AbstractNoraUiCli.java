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

import com.github.noraui.log.annotation.Loggable;

@Loggable
public abstract class AbstractNoraUiCli {

    static Logger log;

    protected static final String RESOURCES = "resources";
    protected static final String ENVIRONMENTS = "environments";

    protected static final String IMPORT_SLF4J_LOGGER = "import org.slf4j.Logger;";
    protected static final String NORAUI_LOGGABLE_ANNOTATION = "@Loggable";
    protected static final String GOOGLE_INJECT_SINGLETON_ANNOTATION = "@Singleton";

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
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

}
