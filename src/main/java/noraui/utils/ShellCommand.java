package noraui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import noraui.exception.TechnicalException;

public class ShellCommand {

    private static final String SHELL_RUNNING_COMMAND = "SHELL_RUNNING_COMMAND";
    private final String command;
    private final String[] parameters;

    private final Logger logger = Logger.getLogger(ShellCommand.class.getClass());

    public ShellCommand(String command, String... parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public int run() throws TechnicalException {
        final Runtime rt = Runtime.getRuntime();
        final List<String> cmdList = new ArrayList<>();
        cmdList.add(command);
        logger.info(String.format(Messages.getMessage(SHELL_RUNNING_COMMAND), command));
        for (final String param : parameters) {
            logger.info(param);
            cmdList.add(param);
        }
        try {
            final Process p = rt.exec(cmdList.toArray(new String[cmdList.size()]));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(line);
            }
            return p.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new TechnicalException(e.getMessage(), e);
        }
    }
}
