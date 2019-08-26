/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;
import static com.github.noraui.utils.Constants.CLI_APPLICATIONS_FILES_DIR;
import static com.github.noraui.utils.Constants.CLI_FILES_DIR;
import static com.github.noraui.utils.Constants.CLI_SCENARIOS_FILES_DIR;
import static com.github.noraui.utils.Messages.CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cli.model.NoraUiApplicationFile;
import com.github.noraui.cli.model.NoraUiCliFile;
import com.github.noraui.cli.model.NoraUiField;
import com.github.noraui.cli.model.NoraUiModel;
import com.github.noraui.cli.model.NoraUiResult;
import com.github.noraui.cli.model.NoraUiScenarioFile;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.service.CryptoService;
import com.github.noraui.service.impl.CryptoServiceImpl;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class NoraUiCommandLineInterface {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NoraUiCommandLineInterface.class);
    private static final String JSON = ".json";
    private static final String CLI_TAB = "    {}) {}";
    private static final String CONTEXT = "Context";

    private Application application;
    private Scenario scenario;
    private Model model;
    private CryptoService cryptoService;

    public NoraUiCommandLineInterface() {
        application = new Application();
        scenario = new Scenario();
        model = new Model();
        cryptoService = new CryptoServiceImpl();
    }

    /**
     * @param context
     *            is generated robot context class.
     * @param counter
     *            is generated robot counter class.
     * @param args
     *            is list of args (-h, --verbose, --update, -interactiveMode, -f, -s, -u, -d, -k, -a, -m, -fi and -re)
     * @throws TechnicalException
     *             is throws if you have a technical error (NoSuchAlgorithmException | NoSuchPaddingException |
     *             InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException,
     *             ...) in NoraUi.
     */
    public void runCli(Class<?> context, Class<?> counter, String... args) throws TechnicalException {
        Scanner input = null;
        boolean runCli = true;
        boolean verbose = false;
        boolean update = false;
        boolean interactiveMode = true;
        int featureCode = -1;
        String applicationName = null;
        String scenarioName = null;
        String modelName = null;
        String url = null;
        String description = null;
        String cryptoKey = null;
        String fields = null;
        String results = null;

        Map<String, String> features = getFeatures();

        displaySplashScreen();
        displayHelp(args, features);

        for (int i = 0; i < args.length; i++) {
            if ("--verbose".equals(args[i])) {
                verbose = true;
            } else if ("--update".equals(args[i])) {
                update = true;
            } else if ("-interactiveMode".equals(args[i])) {
                interactiveMode = Boolean.parseBoolean(args[i + 1]);
            }
        }

        NoraUiCliFile noraUiCliFile = readNoraUiCliFiles(verbose);

        if (!update) {
            if (interactiveMode) {
                input = new Scanner(System.in);
            }
            do {
                featureCode = -1;
                for (int i = 0; i < args.length; i++) {
                    if ("-f".equals(args[i])) {
                        featureCode = Integer.parseInt(args[i + 1]);
                    } else if ("-s".equals(args[i])) {
                        scenarioName = args[i + 1];
                    } else if ("-u".equals(args[i])) {
                        url = args[i + 1];
                    } else if ("-d".equals(args[i])) {
                        description = args[i + 1];
                    } else if ("-a".equals(args[i])) {
                        applicationName = args[i + 1];
                    } else if ("-m".equals(args[i])) {
                        modelName = args[i + 1];
                    } else if ("-fi".equals(args[i])) {
                        fields = args[i + 1];
                    } else if ("-re".equals(args[i])) {
                        results = args[i + 1];
                    } else if ("-k".equals(args[i])) {
                        cryptoKey = args[i + 1];
                    }
                }
                if (!interactiveMode) {
                    if (verbose) {
                        displayCommandLine(args);
                    }
                    runCli = false;
                } else {
                    for (Map.Entry<String, String> f : features.entrySet()) {
                        if (f.getKey().equals(String.valueOf(featureCode))) {
                            LOGGER.info("Do you want {}? Y", f.getValue().toLowerCase());
                            if ("y".equalsIgnoreCase(input.nextLine())) {
                                featureCode = Integer.parseInt(f.getKey());
                                break;
                            }
                        }
                    }
                }

                if (featureCode == -1 && !interactiveMode) {
                    LOGGER.error("When interactiveMode is false, you need use -f");
                } else {
                    if (featureCode == -1) {
                        LOGGER.info("What do you want ?");
                        for (Map.Entry<String, String> f : features.entrySet()) {
                            LOGGER.info("    {} => {}", f.getKey(), f.getValue());
                        }
                        featureCode = input.nextInt();
                        input.nextLine();
                    }
                    if (featureCode > 0) {
                        noraUiCliFile = runFeature(noraUiCliFile, featureCode, applicationName, scenarioName, modelName, url, description, fields, results, cryptoKey, context, counter, verbose, input,
                                interactiveMode);
                        writeNoraUiCliFiles(noraUiCliFile, verbose);
                        displayFooter();
                    } else {
                        runCli = false;
                    }
                }
            } while (runCli);
            if (interactiveMode) {
                input.close();
            }
        } else {
            updateRobotFromNoraUiCliFiles(noraUiCliFile, context, verbose);
        }
        displayEndFooter();
    }

    /**
     * 
     */
    private void displaySplashScreen() {
        LOGGER.info("");
        LOGGER.info("  ███╗   ██╗ ██████╗ ██████╗  █████╗ ██╗   ██╗██╗      ██████╗██╗     ██╗ ");
        LOGGER.info("  ████╗  ██║██╔═══██╗██╔══██╗██╔══██╗██║   ██║██║     ██╔════╝██║     ██║ ");
        LOGGER.info("  ██╔██╗ ██║██║   ██║██████╔╝███████║██║   ██║██║     ██║     ██║     ██║ ");
        LOGGER.info("  ██║╚██╗██║██║   ██║██╔══██╗██╔══██║██║   ██║██║     ██║     ██║     ██║ ");
        LOGGER.info("  ██║ ╚████║╚██████╔╝██║  ██║██║  ██║╚██████╔╝██║     ╚██████╗███████╗██║ ");
        LOGGER.info("  ╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝      ╚═════╝╚══════╝╚═╝ ");
        LOGGER.info("");
        LOGGER.info("  NoraUi Command Line Interface =>");
        LOGGER.info("");
    }

    /**
     * @return a list of feature (Map<index,feature description>).
     */
    private Map<String, String> getFeatures() {
        Map<String, String> features = new HashMap<>();
        features.put("1", "add new application");
        features.put("2", "add new scenario");
        features.put("3", "add new model");
        features.put("4", "remove application");
        features.put("5", "remove scenario");
        features.put("6", "remove model");
        features.put("7", "encrypt data");
        features.put("8", "decrypt data");
        features.put("9", "status");
        features.put("0", "exit NoraUi CLI");
        return features;
    }

    /**
     * @param args
     *            is list of command arguments.
     * @param features
     *            is list of feature (Map<index,feature description>).
     */
    private void displayHelp(String[] args, Map<String, String> features) {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("-help"))) {
            LOGGER.info("-h: Display this help");
            LOGGER.info("--verbose: Add debug informations in console.");
            LOGGER.info("--update: Use NoraUi CLI files for update your robot.");
            LOGGER.info("-f: features");
            for (Map.Entry<String, String> f : features.entrySet()) {
                LOGGER.info("    {} => {}", f.getKey(), f.getValue());
            }
            LOGGER.info("-s: Scenario Name");
            LOGGER.info("-u: Url");
            LOGGER.info("-d: Description");
            LOGGER.info("-k: Crypto key");
            LOGGER.info("-a: Application Name");
            LOGGER.info("-m: Model Name");
            LOGGER.info("-fi: Field list of model");
            LOGGER.info("-re: Result list of model");
            LOGGER.info(
                    "-interactiveMode: (boolean) When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the previously listed parameters. When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command line.");
        }
    }

    /**
     * @param args
     *            is list of command arguments.
     */
    private void displayCommandLine(String[] args) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("Command Line: ");
        for (String arg : args) {
            cmd.append(" ").append(arg);
        }
        LOGGER.info(cmd.toString());
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @return NoraUiCliFile Object contain all data from CLI Files.
     */
    protected NoraUiCliFile readNoraUiCliFiles(boolean verbose) {
        NoraUiCliFile result = new NoraUiCliFile();
        List<NoraUiApplicationFile> noraUiApplicationFiles = new ArrayList<>();
        List<NoraUiScenarioFile> noraUiScenarioFiles = new ArrayList<>();
        Gson gson = new Gson();
        String[] applications = new File(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR).list();
        String[] scenarios = new File(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR).list();
        readApplicationNoraUiCliFiles(verbose, noraUiApplicationFiles, gson, applications);
        readScenarioNoraUiCliFiles(verbose, noraUiScenarioFiles, gson, scenarios);
        result.setApplicationFiles(noraUiApplicationFiles);
        result.setScenarioFiles(noraUiScenarioFiles);
        return result;
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param noraUiApplicationFiles
     *            is list of NoraUiApplicationFile Object.
     * @param gson
     *            is singleton json tool.
     * @param applications
     *            array of application (array of String).
     */
    private void readApplicationNoraUiCliFiles(boolean verbose, List<NoraUiApplicationFile> noraUiApplicationFiles, Gson gson, String[] applications) {
        if (applications != null) {
            for (String app : applications) {
                if (verbose) {
                    LOGGER.info("Application CLI File [{}] found.", app);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + app))) {
                    NoraUiApplicationFile noraUiApplicationFile = gson.fromJson(bufferedReader, NoraUiApplicationFile.class);
                    noraUiApplicationFiles.add(noraUiApplicationFile);
                } catch (IOException e) {
                    LOGGER.error("noraUiApplicationFiles IOException: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param noraUiScenarioFiles
     *            is list of NoraUiScenarioFile Object.
     * @param gson
     *            is singleton json tool.
     * @param scenarios
     *            array of scenario (array of String).
     */
    private void readScenarioNoraUiCliFiles(boolean verbose, List<NoraUiScenarioFile> noraUiScenarioFiles, Gson gson, String[] scenarios) {
        if (scenarios != null) {
            for (String s : scenarios) {
                if (verbose) {
                    LOGGER.info("Scenario CLI File [{}] found.", s);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + s))) {
                    NoraUiScenarioFile noraUiScenarioFile = gson.fromJson(bufferedReader, NoraUiScenarioFile.class);
                    noraUiScenarioFiles.add(noraUiScenarioFile);
                } catch (IOException e) {
                    LOGGER.error("noraUiScenarioFiles IOException: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    protected void writeNoraUiCliFiles(NoraUiCliFile noraUiCliFile, boolean verbose) {
        Gson gson = new Gson();
        writeApplicationsNoraUiCliFiles(noraUiCliFile, verbose, gson);
        writeScenariosNoraUiCliFiles(noraUiCliFile, verbose, gson);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param gson
     *            is singleton json tool.
     */
    private void writeApplicationsNoraUiCliFiles(NoraUiCliFile noraUiCliFile, boolean verbose, Gson gson) {
        for (NoraUiApplicationFile noraUiApplicationFile : noraUiCliFile.getApplicationFiles()) {
            if (new File(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + noraUiApplicationFile.getName() + JSON).exists() && !noraUiApplicationFile.getStatus()) {
                deleteFileApplicationsNoraUiCliFiles(verbose, noraUiApplicationFile);
            }
            if (noraUiApplicationFile.getStatus()) {
                createFileApplicationsNoraUiCliFiles(verbose, gson, noraUiApplicationFile);
                updateFileApplicationsNoraUiCliFiles(gson, noraUiApplicationFile);
            }
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param noraUiApplicationFile
     *            Object contain a application file from CLI Files.
     */
    private void deleteFileApplicationsNoraUiCliFiles(boolean verbose, NoraUiApplicationFile noraUiApplicationFile) {
        try {
            FileUtils.forceDelete(new File(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + noraUiApplicationFile.getName() + JSON));
            if (verbose) {
                LOGGER.info("Application File [{}.json] removed with success.", noraUiApplicationFile.getName());
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param gson
     *            is singleton json tool.
     * @param noraUiApplicationFile
     *            Object contain a application file from CLI Files.
     */
    private void createFileApplicationsNoraUiCliFiles(boolean verbose, Gson gson, NoraUiApplicationFile noraUiApplicationFile) {
        try {
            FileUtils.forceMkdir(new File(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR));
            File applicationFile = new File(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + noraUiApplicationFile.getName() + JSON);
            if (!applicationFile.exists()) {
                Files.asCharSink(applicationFile, StandardCharsets.UTF_8).write(gson.toJson(noraUiApplicationFile));
                if (verbose) {
                    LOGGER.info("Applications File [{}.json] created with success.", noraUiApplicationFile.getName());
                }
            } else {
                if (verbose) {
                    LOGGER.info("Applications File [{}.json] already exist.", noraUiApplicationFile.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param gson
     *            is singleton json tool.
     * @param noraUiApplicationFile
     *            Object contain a application file from CLI Files.
     */
    private void updateFileApplicationsNoraUiCliFiles(Gson gson, NoraUiApplicationFile noraUiApplicationFile) {
        try (FileWriter fw = new FileWriter(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + noraUiApplicationFile.getName() + JSON)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(gson.toJson(noraUiApplicationFile));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param gson
     *            is singleton json tool.
     */
    private void writeScenariosNoraUiCliFiles(NoraUiCliFile noraUiCliFile, boolean verbose, Gson gson) {
        for (NoraUiScenarioFile noraUiScenarioFile : noraUiCliFile.getScenarioFiles()) {
            if (new File(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + noraUiScenarioFile.getName() + JSON).exists() && !noraUiScenarioFile.getStatus()) {
                deleteFileScenarioNoraUiCliFiles(verbose, noraUiScenarioFile);
            }
            if (noraUiScenarioFile.getStatus()) {
                createFileScenarioNoraUiCliFiles(verbose, gson, noraUiScenarioFile);
                updateFileScenarioNoraUiCliFiles(gson, noraUiScenarioFile);
            }
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param noraUiScenarioFile
     *            Object contain a scenario file from CLI Files.
     */
    private void deleteFileScenarioNoraUiCliFiles(boolean verbose, NoraUiScenarioFile noraUiScenarioFile) {
        try {
            FileUtils.forceDelete(new File(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + noraUiScenarioFile.getName() + JSON));
            if (verbose) {
                LOGGER.info("Scenario File [{}.json] removed with success.", noraUiScenarioFile.getName());
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param gson
     *            is singleton json tool.
     * @param noraUiScenarioFile
     *            Object contain a scenario file from CLI Files.
     */
    private void createFileScenarioNoraUiCliFiles(boolean verbose, Gson gson, NoraUiScenarioFile noraUiScenarioFile) {
        try {
            FileUtils.forceMkdir(new File(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR));
            File applicationFile = new File(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + noraUiScenarioFile.getName() + JSON);
            if (!applicationFile.exists()) {
                Files.asCharSink(applicationFile, StandardCharsets.UTF_8).write(gson.toJson(noraUiScenarioFile));
                if (verbose) {
                    LOGGER.info("Scenario File [{}.json] created with success.", noraUiScenarioFile.getName());
                }
            } else {
                if (verbose) {
                    LOGGER.info("Scenario File [{}.json] already exist.", noraUiScenarioFile.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param gson
     *            is singleton json tool.
     * @param noraUiScenarioFile
     *            Object contain a scenario file from CLI Files.
     */
    private void updateFileScenarioNoraUiCliFiles(Gson gson, NoraUiScenarioFile noraUiScenarioFile) {
        try (FileWriter fw = new FileWriter(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + noraUiScenarioFile.getName() + JSON)) {
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(gson.toJson(noraUiScenarioFile));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            LOGGER.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     */
    private void updateRobotFromNoraUiCliFiles(NoraUiCliFile noraUiCliFile, Class<?> robotContext, boolean verbose) {
        LOGGER.info("updateRobotFromNoraUiCliFiles");
        for (NoraUiApplicationFile noraUiApplicationFile : noraUiCliFile.getApplicationFiles()) {
            addApplication(null, noraUiApplicationFile.getName(), noraUiApplicationFile.getUrl(), robotContext, verbose, null, false);
            for (NoraUiModel noraUiModel : noraUiApplicationFile.getModels()) {
                addModel(null, noraUiApplicationFile.getName(), noraUiModel.getName(), noraUiModel.getFieldsString(), noraUiModel.getResultsString(), robotContext, verbose, null, false);
            }
        }
        for (NoraUiScenarioFile noraUiScenarioFile : noraUiCliFile.getScenarioFiles()) {
            addScenario(null, noraUiScenarioFile.getApplication(), noraUiScenarioFile.getName(), noraUiScenarioFile.getDescription(), robotContext.getSimpleName().replace(CONTEXT, ""), verbose,
                    null, false);
        }

    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param featureCode
     *            is -f arg ("1" is "add new application", "2" is "add new scenario", "3" is "add new model", "4" is
     *            "remove application", "5" is "remove scenario", "6" is "remove model", "7" is
     *            "encrypt data", "8" is "decrypt data", "9" is "status" and "0" is "exit NoraUi CLI").
     * @param applicationName
     *            name of application.
     * @param scenarioName
     *            name of scenario.
     * @param modelName
     *            name of model.
     * @param url
     *            is first(home or login page) url of application.
     * @param description
     *            is description of scenario.
     * @param fields
     *            is fields of model (String separated by a space).
     * @param results
     *            is results of model (String separated by a space).
     * @param cryptoKey
     *            is AES key (secret key).
     * @param robotContext
     *            Context class from robot.
     * @param robotCounter
     *            Counter class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     * @return NoraUiCliFile Object contain all data from CLI Files.
     * @throws TechnicalException
     *             is throws if you have a technical error (NoSuchAlgorithmException | NoSuchPaddingException |
     *             InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException,
     *             ...) in NoraUi.
     */
    private NoraUiCliFile runFeature(NoraUiCliFile noraUiCliFile, int featureCode, String applicationName, String scenarioName, String modelName, String url, String description, String fields,
            String results, String cryptoKey, Class<?> robotContext, Class<?> robotCounter, boolean verbose, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (featureCode == 1) {
            addApplication(noraUiCliFile, applicationName, url, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 2) {
            addScenario(noraUiCliFile, applicationName, scenarioName, description, robotContext.getSimpleName().replace(CONTEXT, ""), verbose, input, interactiveMode);
        } else if (featureCode == 3) {
            addModel(noraUiCliFile, applicationName, modelName, fields, results, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 4) {
            removeApplication(noraUiCliFile, applicationName, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 5) {
            removeScenario(noraUiCliFile, scenarioName, robotContext.getSimpleName().replace(CONTEXT, ""), robotCounter, verbose, input, interactiveMode);
        } else if (featureCode == 6) {
            removeModel(noraUiCliFile, applicationName, modelName, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 7) {
            encrypt(cryptoKey, description, input, interactiveMode);
        } else if (featureCode == 8) {
            decrypt(cryptoKey, description, input, interactiveMode);
        } else if (featureCode == 9) {
            status(noraUiCliFile);
        }
        return noraUiCliFile;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application added.
     * @param url
     *            is first(home or login page) url of application.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void addApplication(NoraUiCliFile noraUiCliFile, String applicationName, String url, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                LOGGER.info("Enter application name:");
                applicationName = input.nextLine();
            }
            if (url == null || "".equals(url)) {
                LOGGER.info("Enter url:");
                url = input.nextLine();
            }
            application.add(applicationName, url, robotContext, verbose);
        } else {
            if (applicationName == null || "".equals(applicationName) || url == null || "".equals(url)) {
                LOGGER.error("When you want to add an application with interactiveMode is false, you need use -a and -u");
            } else {
                application.add(applicationName, url, robotContext, verbose);
            }
        }
        addApplication4CliFiles(noraUiCliFile, applicationName, url);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application added.
     * @param url
     *            is first(home or login page) url of application.
     */
    private void addApplication4CliFiles(NoraUiCliFile noraUiCliFile, String applicationName, String url) {
        NoraUiApplicationFile noraUiApplicationFile = new NoraUiApplicationFile();
        noraUiApplicationFile.setName(applicationName);
        noraUiApplicationFile.setUrl(url);
        if (noraUiCliFile != null) {
            List<NoraUiApplicationFile> r = noraUiCliFile.addApplication(noraUiApplicationFile);
            noraUiCliFile.setApplicationFiles(r);
        }
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param scenarioName
     *            name of scenario added.
     * @param description
     *            is description of scenario.
     * @param robotName
     *            is name of target Robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void addScenario(NoraUiCliFile noraUiCliFile, String applicationName, String scenarioName, String description, String robotName, boolean verbose, Scanner input,
            boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                } else {
                    LOGGER.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (applicationName != null && !"".equals(applicationName)) {
                if (scenarioName == null || "".equals(scenarioName)) {
                    LOGGER.info("Enter scenario name:");
                    scenarioName = input.nextLine();
                }
                if (description == null || "".equals(description)) {
                    LOGGER.info("Enter description:");
                    description = input.nextLine();
                }
                scenario.add(scenarioName, description, applicationName, robotName, verbose);
            }
        } else {
            if (scenarioName == null || "".equals(scenarioName) || description == null || "".equals(description) || applicationName == null || "".equals(applicationName)) {
                LOGGER.error("When you want to add a scenario with interactiveMode is false, you need use -a, -s and -d");
            } else {
                if (isApplicationFound(applicationName)) {
                    scenario.add(scenarioName, description, applicationName, robotName, verbose);
                }
            }
        }
        addScenario4CliFiles(noraUiCliFile, applicationName, scenarioName, description);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param scenarioName
     *            name of scenario added.
     * @param description
     *            is description of scenario.
     */
    private void addScenario4CliFiles(NoraUiCliFile noraUiCliFile, String applicationName, String scenarioName, String description) {
        if (noraUiCliFile != null) {
            NoraUiScenarioFile noraUiScenarioFile = new NoraUiScenarioFile();
            noraUiScenarioFile.setName(scenarioName);
            noraUiScenarioFile.setDescription(description);
            noraUiScenarioFile.setApplication(applicationName);
            List<NoraUiScenarioFile> r = noraUiCliFile.addScenario(noraUiScenarioFile);
            noraUiCliFile.setScenarioFiles(r);
        }
    }

    /**
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param appList
     *            list of application (string list).
     * @return index of application (int in a String).
     */
    private String askApplicationNumber(Scanner input, List<String> appList) {
        String applicationName;
        LOGGER.info("Enter index application number:");
        for (int i = 0; i < appList.size(); i++) {
            LOGGER.info(CLI_TAB, i + 1, appList.get(i));
        }
        int appCode = input.nextInt();
        input.nextLine();
        applicationName = appList.get(appCode - 1);
        return applicationName;
    }

    /**
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param scenarioList
     *            list of scenario (string list).
     * @return index of scenario (int in a String).
     */
    private String askScenarioNumber(Scanner input, List<String> scenarioList) {
        String scenarioName;
        LOGGER.info("Enter index scenario number:");
        for (int i = 0; i < scenarioList.size(); i++) {
            LOGGER.info(CLI_TAB, i + 1, scenarioList.get(i));
        }
        int scenarioCode = input.nextInt();
        input.nextLine();
        scenarioName = scenarioList.get(scenarioCode - 1);
        return scenarioName;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model added.
     * @param fields
     *            is fields of model (String separated by a space).
     * @param results
     *            is results of model (String separated by a space).
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void addModel(NoraUiCliFile noraUiCliFile, String applicationName, String modelName, String fields, String results, Class<?> robotContext, boolean verbose, Scanner input,
            boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                } else {
                    LOGGER.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (applicationName != null && !"".equals(applicationName)) {
                if (modelName == null || "".equals(modelName)) {
                    LOGGER.info("Enter model name:");
                    modelName = input.nextLine();
                }
                if (fields == null || "".equals(fields)) {
                    LOGGER.info("Enter field list:");
                    fields = input.nextLine();
                }
                if (results == null || "".equals(results)) {
                    LOGGER.info("Enter result list (optional):");
                    results = input.nextLine();
                    if ("".equals(results)) {
                        results = null;
                    }
                }
                model.add(applicationName, modelName, fields, results, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName) || fields == null || "".equals(fields)) {
                LOGGER.error("When you want to add a model with interactiveMode is false, you need use -a, -m, -fi and -re (optional)");
            } else {
                if (isApplicationFound(applicationName)) {
                    model.add(applicationName, modelName, fields, results, robotContext, verbose);
                }
            }
        }
        addModel4CliFiles(noraUiCliFile, applicationName, modelName, fields, results);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model added.
     * @param fields
     *            is fields of model (String separated by a space).
     * @param results
     *            is results of model (String separated by a space).
     */
    private void addModel4CliFiles(NoraUiCliFile noraUiCliFile, String applicationName, String modelName, String fields, String results) {
        if (noraUiCliFile != null) {
            NoraUiModel noraUiModel = new NoraUiModel();
            noraUiModel.setName(modelName);
            noraUiModel.setFields(fields);
            noraUiModel.setResults(results);
            List<NoraUiApplicationFile> r = noraUiCliFile.addModel(applicationName, noraUiModel);
            noraUiCliFile.setApplicationFiles(r);
        }
    }

    /**
     * Is application found looking for if applicationName match with an existing application.
     * 
     * @param applicationName
     *            is the name of the application you are looking for.
     * @return true if applicationName match with an existing application.
     */
    private boolean isApplicationFound(String applicationName) {
        boolean applicationFinded = false;
        List<String> appList = application.get();
        if (!appList.isEmpty()) {
            if (appList.contains(applicationName)) {
                applicationFinded = true;
            } else {
                LOGGER.info("Application [{}] do not exist. You must create an application named [{}] first.", applicationName, applicationName);
            }
        } else {
            LOGGER.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
        }
        return applicationFinded;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void removeApplication(NoraUiCliFile noraUiCliFile, String applicationName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                } else {
                    LOGGER.info("Your robot does not contain applications.");
                }
            }
            if (applicationName != null && !"".equals(applicationName)) {
                application.remove(applicationName, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName)) {
                LOGGER.error("When you want to remove an application with interactiveMode is false, you need use -a");
            } else {
                application.remove(applicationName, robotContext, verbose);
            }
        }
        removeApplication4CliFiles(noraUiCliFile, applicationName);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application removed.
     */
    private void removeApplication4CliFiles(NoraUiCliFile noraUiCliFile, String applicationName) {
        if (applicationName != null && !"".equals(applicationName)) {
            List<NoraUiApplicationFile> r = noraUiCliFile.removeApplication(applicationName);
            noraUiCliFile.setApplicationFiles(r);
        }
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param scenarioName
     *            name of scenario.
     * @param robotName
     *            is name of target Robot.
     * @param robotCounter
     *            Counter class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     * @return NoraUiCliFile Object contain all data from CLI Files.
     */
    private void removeScenario(NoraUiCliFile noraUiCliFile, String scenarioName, String robotName, Class<?> robotCounter, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean scenarioFinded = false;
            if (scenarioName == null || "".equals(scenarioName)) {
                List<String> scenarioList = scenario.get();
                if (!scenarioList.isEmpty()) {
                    scenarioName = askScenarioNumber(input, scenarioList);
                    scenarioFinded = true;
                } else {
                    LOGGER.info("Your robot does not contain scenarios.");
                }
            }
            if (scenarioFinded) {
                scenario.remove(scenarioName, robotName, robotCounter, verbose);
            }
        } else {
            if (scenarioName == null || "".equals(scenarioName)) {
                LOGGER.error("When you want to remove a scenario with interactiveMode is false, you need use -s");
            } else {
                scenario.remove(scenarioName, robotName, robotCounter, verbose);
            }
        }
        removeScenario4CliFiles(noraUiCliFile, scenarioName);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param scenarioName
     *            name of scenario removed.
     * @return NoraUiCliFile Object contain all data from CLI Files.
     */
    private void removeScenario4CliFiles(NoraUiCliFile noraUiCliFile, String scenarioName) {
        if (scenarioName != null && !"".equals(scenarioName)) {
            List<NoraUiScenarioFile> r = noraUiCliFile.removeScenario(scenarioName);
            noraUiCliFile.setScenarioFiles(r);
        }
        // return noraUiCliFile;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model removed.
     * @param robotContext
     *            Context class from robot.
     * @param verbose
     *            boolean to activate verbose mode (show more traces).
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void removeModel(NoraUiCliFile noraUiCliFile, String applicationName, String modelName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            boolean modelFinded = false;
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName)) {
                List<String> appList = model.getApplications(robotContext);
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                    applicationFinded = true;
                } else {
                    LOGGER.info("Your robot does not contain applications.");
                }

                List<String> modelList = model.getModels(applicationName, robotContext);
                if (!modelList.isEmpty()) {
                    LOGGER.info("Enter index model number:");
                    for (int i = 0; i < modelList.size(); i++) {
                        LOGGER.info(CLI_TAB, i + 1, modelList.get(i));
                    }
                    int modelCode = input.nextInt();
                    input.nextLine();
                    modelName = modelList.get(modelCode - 1);
                    modelFinded = true;
                } else {
                    LOGGER.info("Your robot does not contain models.");
                }
            }
            if (applicationFinded && modelFinded) {
                model.remove(applicationName, modelName, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName)) {
                LOGGER.error("When you want to remove a model with interactiveMode is false, you need use -a and -m");
            } else {
                model.remove(applicationName, modelName, robotContext, verbose);
            }
        }
        removeModelInCliFileFeature(noraUiCliFile, applicationName, modelName);
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param applicationName
     *            name of application.
     * @param modelName
     *            name of model removed.
     */
    private void removeModelInCliFileFeature(NoraUiCliFile noraUiCliFile, String applicationName, String modelName) {
        if (applicationName != null && !"".equals(applicationName)) {
            List<NoraUiApplicationFile> r = noraUiCliFile.removeModel(applicationName, modelName);
            noraUiCliFile.setApplicationFiles(r);
        }
    }

    /**
     * @param cryptoKey
     *            is AES key (secret key).
     * @param description
     *            is description of scenario.
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     * @throws TechnicalException
     *             is throws if you have a technical error (NoSuchAlgorithmException | NoSuchPaddingException |
     *             InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException,
     *             ...) in NoraUi.
     */
    private void encrypt(String cryptoKey, String data, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (interactiveMode) {
            if (cryptoKey == null || "".equals(cryptoKey)) {
                LOGGER.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (data == null || "".equals(data)) {
                LOGGER.info("Enter data:");
                data = input.nextLine();
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
                LOGGER.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
            }
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || data == null || "".equals(data)) {
                LOGGER.error("When you want to encrypt data with interactiveMode is false, you need use -d and -k");
            } else if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
                LOGGER.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
            }
        }

    }

    /**
     * @param cryptoKey
     *            is AES key (secret key).
     * @param description
     *            is description of scenario.
     * @param input
     *            NoraUI CLI use Java Scanner class.
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     * @throws TechnicalException
     *             is throws if you have a technical error (NoSuchAlgorithmException | NoSuchPaddingException |
     *             InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException,
     *             ...) in NoraUi.
     */
    private void decrypt(String cryptoKey, String description, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (interactiveMode) {
            if (cryptoKey == null || "".equals(cryptoKey)) {
                LOGGER.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (description == null || "".equals(description)) {
                LOGGER.info("Enter data:");
                description = input.nextLine();
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
                LOGGER.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
            }
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || description == null || "".equals(description)) {
                LOGGER.error("When you want to decrypt data with interactiveMode is false, you need use -d and -k");
            } else if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
                LOGGER.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
            }
        }
    }

    /**
     * CLI status feature display all datas from NoraUi CLI files (.noraui folder at the root of robot).
     * 
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     */
    private void status(NoraUiCliFile noraUiCliFile) {
        List<NoraUiApplicationFile> applications = noraUiCliFile.getApplicationFiles();
        for (NoraUiApplicationFile a : applications) {
            LOGGER.info("Application: [{}]", a.getName());
            LOGGER.info(" - url: [{}]", a.getUrl());
            for (NoraUiModel m : a.getModels()) {
                LOGGER.info(" - model: [{}]", m.getName());
                for (NoraUiField field : m.getFields()) {
                    LOGGER.info("   - field: [{}]", field.getName());
                }
                for (NoraUiResult result : m.getResults()) {
                    LOGGER.info("   - result: [{}]", result.getName());
                }
            }
        }
        List<NoraUiScenarioFile> scenarios = noraUiCliFile.getScenarioFiles();
        for (NoraUiScenarioFile s : scenarios) {
            LOGGER.info("Scenario: [{}]", s.getName());
            LOGGER.info(" - description: [{}]", s.getDescription());
            LOGGER.info(" - application: [{}]", s.getApplication());
        }
    }

    /**
     * Display CLI footer.
     */
    private void displayFooter() {
        LOGGER.info("");
        LOGGER.info("NoraUi Command Line Interface finished with success.");
        LOGGER.info("");
    }

    /**
     * Display end CLI footer.
     */
    private void displayEndFooter() {
        LOGGER.info("");
        LOGGER.info("Exit NoraUi Command Line Interface with success.");
        LOGGER.info("");
    }

    // setter for Mock
    protected void setApplication(Application application) {
        this.application = application;
    }

    protected void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    protected void setModel(Model model) {
        this.model = model;
    }

}
