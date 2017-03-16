package noraui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import noraui.exception.TechnicalException;

public class ShellCommand {
    private String command;
    private String[] parameters;

    private final Logger logger = Logger.getLogger(ShellCommand.class.getClass());

    public ShellCommand(String command, String... parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public int run() throws TechnicalException {
        Runtime rt = Runtime.getRuntime();
        List<String> cmdList = new ArrayList<>();
        cmdList.add(command);
        logger.info("Launched Command: " + command + " with following parameters:");
        for (String param : parameters) {
            logger.info(param);
            cmdList.add(param);
        }
        try {
            Process p = rt.exec(cmdList.toArray(new String[cmdList.size()]));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

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
