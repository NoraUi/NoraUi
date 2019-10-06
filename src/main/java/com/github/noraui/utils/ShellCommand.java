/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;

@Loggable
public class ShellCommand {

    static Logger LOGGER;

    private static final String SHELL_RUNNING_COMMAND = "SHELL_RUNNING_COMMAND";
    private final String command;
    private final String[] parameters;

    public ShellCommand(String command, String... parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public int run() throws TechnicalException {
        final Runtime rt = Runtime.getRuntime();
        final List<String> cmdList = new ArrayList<>();
        cmdList.add(command);
        LOGGER.info(Messages.getMessage(SHELL_RUNNING_COMMAND), command);
        Stream.of(parameters).forEach(param -> {
            LOGGER.info(param);
            cmdList.add(param);
        });
        try {
            final Process p = rt.exec(cmdList.toArray(new String[cmdList.size()]));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
            }
            return p.waitFor();
        } catch (IOException e) {
            LOGGER.error("IOException error ShellCommand.run():");
            throw new TechnicalException(e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException error ShellCommand.run():");
            Thread.currentThread().interrupt();
            throw new TechnicalException(e.getMessage(), e);
        }
    }
}
