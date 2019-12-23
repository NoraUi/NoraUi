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
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.github.noraui.cli.model.NoraUiApplicationFile;
import com.github.noraui.cli.model.NoraUiCliFile;
import com.github.noraui.cli.model.NoraUiCliParameters;
import com.github.noraui.cli.model.NoraUiField;
import com.github.noraui.cli.model.NoraUiModel;
import com.github.noraui.cli.model.NoraUiResult;
import com.github.noraui.cli.model.NoraUiScenarioFile;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.NoraUiLoggingInjector;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.CryptoService;
import com.github.noraui.service.impl.CryptoServiceImpl;
import com.github.noraui.utils.Constants;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Loggable
public class NoraUiCommandLineInterface {

    static Logger log;

    private static final String JSON = ".json";
    private static final String CLI_TAB = "    {}) {}";
    private static final String CONTEXT = "Context";

    private Application application;
    private Scenario scenario;
    private Model model;
    private CryptoService cryptoService;

    public NoraUiCommandLineInterface() {
        NoraUiLoggingInjector.addInjector(Constants.TOP_LEVEL_PACKAGE);
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
                            log.info("Do you want {}? Y", f.getValue().toLowerCase());
                            if ("y".equalsIgnoreCase(input.nextLine())) {
                                featureCode = Integer.parseInt(f.getKey());
                                break;
                            }
                        }
                    }
                }

                if (featureCode == -1 && !interactiveMode) {
                    log.error("When interactiveMode is false, you need use -f");
                } else {
                    if (featureCode == -1) {
                        log.info("What do you want ?");
                        for (Map.Entry<String, String> f : features.entrySet()) {
                            log.info("    {} => {}", f.getKey(), f.getValue());
                        }
                        featureCode = input.nextInt();
                        input.nextLine();
                    }
                    if (featureCode > 0) {
                        noraUiCliFile = runFeature(noraUiCliFile, new NoraUiCliParameters(featureCode, applicationName, scenarioName, modelName, url, description, fields, results, cryptoKey, context,
                                counter, verbose, input, interactiveMode));
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
        NoraUiLoggingInjector.resetInjector();
    }

    /**
     * 
     */
    private void displaySplashScreen() {
        log.info("");
        log.info("  ███╗   ██╗ ██████╗ ██████╗  █████╗ ██╗   ██╗██╗      ██████╗██╗     ██╗ ");
        log.info("  ████╗  ██║██╔═══██╗██╔══██╗██╔══██╗██║   ██║██║     ██╔════╝██║     ██║ ");
        log.info("  ██╔██╗ ██║██║   ██║██████╔╝███████║██║   ██║██║     ██║     ██║     ██║ ");
        log.info("  ██║╚██╗██║██║   ██║██╔══██╗██╔══██║██║   ██║██║     ██║     ██║     ██║ ");
        log.info("  ██║ ╚████║╚██████╔╝██║  ██║██║  ██║╚██████╔╝██║     ╚██████╗███████╗██║ ");
        log.info("  ╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝      ╚═════╝╚══════╝╚═╝ ");
        log.info("");
        log.info("  NoraUi Command Line Interface =>");
        log.info("");
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
            log.info("-h: Display this help");
            log.info("--verbose: Add debug informations in console.");
            log.info("--update: Use NoraUi CLI files for update your robot.");
            log.info("-f: features");
            for (Map.Entry<String, String> f : features.entrySet()) {
                log.info("    {} => {}", f.getKey(), f.getValue());
            }
            log.info("-s: Scenario Name");
            log.info("-u: Url");
            log.info("-d: Description");
            log.info("-k: Crypto key");
            log.info("-a: Application Name");
            log.info("-m: Model Name");
            log.info("-fi: Field list of model");
            log.info("-re: Result list of model");
            log.info(
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
        log.info(cmd.toString());
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
                    log.info("Application CLI File [{}] found.", app);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + CLI_APPLICATIONS_FILES_DIR + File.separator + app))) {
                    NoraUiApplicationFile noraUiApplicationFile = gson.fromJson(bufferedReader, NoraUiApplicationFile.class);
                    noraUiApplicationFiles.add(noraUiApplicationFile);
                } catch (IOException e) {
                    log.error("noraUiApplicationFiles IOException: {}", e.getMessage(), e);
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
                    log.info("Scenario CLI File [{}] found.", s);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + CLI_SCENARIOS_FILES_DIR + File.separator + s))) {
                    NoraUiScenarioFile noraUiScenarioFile = gson.fromJson(bufferedReader, NoraUiScenarioFile.class);
                    noraUiScenarioFiles.add(noraUiScenarioFile);
                } catch (IOException e) {
                    log.error("noraUiScenarioFiles IOException: {}", e.getMessage(), e);
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
                log.info("Application File [{}.json] removed with success.", noraUiApplicationFile.getName());
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
                    log.info("Applications File [{}.json] created with success.", noraUiApplicationFile.getName());
                }
            } else {
                if (verbose) {
                    log.info("Applications File [{}.json] already exist.", noraUiApplicationFile.getName());
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
                log.info("Scenario File [{}.json] removed with success.", noraUiScenarioFile.getName());
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
                    log.info("Scenario File [{}.json] created with success.", noraUiScenarioFile.getName());
                }
            } else {
                if (verbose) {
                    log.info("Scenario File [{}.json] already exist.", noraUiScenarioFile.getName());
                }
            }
        } catch (Exception e) {
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
            log.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
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
    private void updateRobotFromNoraUiCliFiles(NoraUiCliFile noraUiCliFile, Class<?> context, boolean verbose) {
        log.info("updateRobotFromNoraUiCliFiles");
        for (NoraUiApplicationFile noraUiApplicationFile : noraUiCliFile.getApplicationFiles()) {
            addApplication(null, new NoraUiCliParameters(-1, noraUiApplicationFile.getName(), null, null, noraUiApplicationFile.getUrl(), null, null, null, null, context, null, verbose, null, false));
            for (NoraUiModel noraUiModel : noraUiApplicationFile.getModels()) {
                addModel(null, new NoraUiCliParameters(-1, noraUiApplicationFile.getName(), null, noraUiModel.getName(), null, null, noraUiModel.getFieldsString(), noraUiModel.getResultsString(),
                        null, context, null, verbose, null, false));
            }
        }
        for (NoraUiScenarioFile noraUiScenarioFile : noraUiCliFile.getScenarioFiles()) {
            addScenario(null, new NoraUiCliParameters(-1, noraUiScenarioFile.getApplication(), noraUiScenarioFile.getName(), null, null, noraUiScenarioFile.getDescription(), null, null, null, context,
                    null, verbose, null, false));
        }

    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @return NoraUiCliFile Object contain all data from CLI Files.
     * @throws TechnicalException
     *             is throws if you have a technical error (NoSuchAlgorithmException | NoSuchPaddingException |
     *             InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException,
     *             ...) in NoraUi.
     */
    private NoraUiCliFile runFeature(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) throws TechnicalException {
        if (noraUiCliParameters.getFeatureCode() == 1) {
            addApplication(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 2) {
            addScenario(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 3) {
            addModel(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 4) {
            removeApplication(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 5) {
            removeScenario(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 6) {
            removeModel(noraUiCliFile, noraUiCliParameters);
        } else if (noraUiCliParameters.getFeatureCode() == 7) {
            encrypt(noraUiCliParameters.getCryptoKey(), noraUiCliParameters.getDescription(), noraUiCliParameters.getInput(), noraUiCliParameters.getInteractiveMode());
        } else if (noraUiCliParameters.getFeatureCode() == 8) {
            decrypt(noraUiCliParameters.getCryptoKey(), noraUiCliParameters.getDescription(), noraUiCliParameters.getInput(), noraUiCliParameters.getInteractiveMode());
        } else if (noraUiCliParameters.getFeatureCode() == 9) {
            status(noraUiCliFile);
        }
        return noraUiCliFile;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void addApplication(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                log.info("Enter application name:");
                noraUiCliParameters.setApplicationName(noraUiCliParameters.getInput().nextLine());
            }
            if (noraUiCliParameters.getUrl() == null || "".equals(noraUiCliParameters.getUrl())) {
                log.info("Enter url:");
                noraUiCliParameters.setUrl(noraUiCliParameters.getInput().nextLine());
            }
            application.add(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getUrl(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
        } else {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName()) || noraUiCliParameters.getUrl() == null
                    || "".equals(noraUiCliParameters.getUrl())) {
                log.error("When you want to add an application with interactiveMode is false, you need use -a and -u");
            } else {
                application.add(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getUrl(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        }
        addApplication4CliFiles(noraUiCliFile, noraUiCliParameters.getApplicationName(), noraUiCliParameters.getUrl());
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
    private void addScenario(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    noraUiCliParameters.setApplicationName(askApplicationNumber(noraUiCliParameters.getInput(), appList));
                } else {
                    log.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (noraUiCliParameters.getApplicationName() != null && !"".equals(noraUiCliParameters.getApplicationName())) {
                if (noraUiCliParameters.getScenarioName() == null || "".equals(noraUiCliParameters.getScenarioName())) {
                    log.info("Enter scenario name:");
                    noraUiCliParameters.setScenarioName(noraUiCliParameters.getInput().nextLine());
                }
                if (noraUiCliParameters.getDescription() == null || "".equals(noraUiCliParameters.getDescription())) {
                    log.info("Enter description:");
                    noraUiCliParameters.setDescription(noraUiCliParameters.getInput().nextLine());
                }
                scenario.add(noraUiCliParameters.getScenarioName(), noraUiCliParameters.getDescription(), noraUiCliParameters.getApplicationName(),
                        noraUiCliParameters.getRobotContext().getSimpleName().replace(CONTEXT, ""), noraUiCliParameters.getVerbose());
            }
        } else {
            if (noraUiCliParameters.getScenarioName() == null || "".equals(noraUiCliParameters.getScenarioName()) || noraUiCliParameters.getDescription() == null
                    || "".equals(noraUiCliParameters.getDescription()) || noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                log.error("When you want to add a scenario with interactiveMode is false, you need use -a, -s and -d");
            } else {
                if (isApplicationFound(noraUiCliParameters.getApplicationName())) {
                    scenario.add(noraUiCliParameters.getScenarioName(), noraUiCliParameters.getDescription(), noraUiCliParameters.getApplicationName(),
                            noraUiCliParameters.getRobotContext().getSimpleName().replace(CONTEXT, ""), noraUiCliParameters.getVerbose());
                }
            }
        }
        addScenario4CliFiles(noraUiCliFile, noraUiCliParameters.getApplicationName(), noraUiCliParameters.getScenarioName(), noraUiCliParameters.getDescription());
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
        log.info("Enter index application number:");
        for (int i = 0; i < appList.size(); i++) {
            log.info(CLI_TAB, i + 1, appList.get(i));
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
        log.info("Enter index scenario number:");
        for (int i = 0; i < scenarioList.size(); i++) {
            log.info(CLI_TAB, i + 1, scenarioList.get(i));
        }
        int scenarioCode = input.nextInt();
        input.nextLine();
        scenarioName = scenarioList.get(scenarioCode - 1);
        return scenarioName;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void addModel(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    noraUiCliParameters.setApplicationName(askApplicationNumber(noraUiCliParameters.getInput(), appList));
                } else {
                    log.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (noraUiCliParameters.getApplicationName() != null && !"".equals(noraUiCliParameters.getApplicationName())) {
                if (noraUiCliParameters.getModelName() == null || "".equals(noraUiCliParameters.getModelName())) {
                    log.info("Enter model name:");
                    noraUiCliParameters.setModelName(noraUiCliParameters.getInput().nextLine());
                }
                if (noraUiCliParameters.getFields() == null || "".equals(noraUiCliParameters.getFields())) {
                    log.info("Enter field list:");
                    noraUiCliParameters.setFields(noraUiCliParameters.getInput().nextLine());
                }
                if (noraUiCliParameters.getResults() == null || "".equals(noraUiCliParameters.getResults())) {
                    log.info("Enter result list (optional):");
                    noraUiCliParameters.setResults(noraUiCliParameters.getInput().nextLine());
                    if ("".equals(noraUiCliParameters.getResults())) {
                        noraUiCliParameters.setResults(null);
                    }
                }
                model.add(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName(), noraUiCliParameters.getFields(), noraUiCliParameters.getResults(),
                        noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        } else {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName()) || noraUiCliParameters.getModelName() == null
                    || "".equals(noraUiCliParameters.getModelName()) || noraUiCliParameters.getFields() == null || "".equals(noraUiCliParameters.getFields())) {
                log.error("When you want to add a model with interactiveMode is false, you need use -a, -m, -fi and -re (optional)");
            } else {
                if (isApplicationFound(noraUiCliParameters.getApplicationName())) {
                    model.add(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName(), noraUiCliParameters.getFields(), noraUiCliParameters.getResults(),
                            noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
                }
            }
        }
        addModel4CliFiles(noraUiCliFile, noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName(), noraUiCliParameters.getFields(), noraUiCliParameters.getResults());
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
                log.info("Application [{}] do not exist. You must create an application named [{}] first.", applicationName, applicationName);
            }
        } else {
            log.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
        }
        return applicationFinded;
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void removeApplication(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    noraUiCliParameters.setApplicationName(askApplicationNumber(noraUiCliParameters.getInput(), appList));
                } else {
                    log.info("Your robot does not contain applications.");
                }
            }
            if (noraUiCliParameters.getApplicationName() != null && !"".equals(noraUiCliParameters.getApplicationName())) {
                application.remove(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        } else {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName())) {
                log.error("When you want to remove an application with interactiveMode is false, you need use -a");
            } else {
                application.remove(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        }
        removeApplication4CliFiles(noraUiCliFile, noraUiCliParameters.getApplicationName());
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
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @return NoraUiCliFile Object contain all data from CLI Files.
     */
    private void removeScenario(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            boolean scenarioFinded = false;
            if (noraUiCliParameters.getScenarioName() == null || "".equals(noraUiCliParameters.getScenarioName())) {
                List<String> scenarioList = scenario.get();
                if (!scenarioList.isEmpty()) {
                    noraUiCliParameters.setScenarioName(askScenarioNumber(noraUiCliParameters.getInput(), scenarioList));
                    scenarioFinded = true;
                } else {
                    log.info("Your robot does not contain scenarios.");
                }
            }
            if (scenarioFinded) {
                scenario.remove(noraUiCliParameters.getScenarioName(), noraUiCliParameters.getRobotContext().getSimpleName().replace(CONTEXT, ""), noraUiCliParameters.getRobotCounter(),
                        noraUiCliParameters.getVerbose());
            }
        } else {
            if (noraUiCliParameters.getScenarioName() == null || "".equals(noraUiCliParameters.getScenarioName())) {
                log.error("When you want to remove a scenario with interactiveMode is false, you need use -s");
            } else {
                scenario.remove(noraUiCliParameters.getScenarioName(), noraUiCliParameters.getRobotContext().getSimpleName().replace(CONTEXT, ""), noraUiCliParameters.getRobotCounter(),
                        noraUiCliParameters.getVerbose());
            }
        }
        removeScenario4CliFiles(noraUiCliFile, noraUiCliParameters.getScenarioName());
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
    }

    /**
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     * @param noraUiCliParameters
     *            contains all NoraUi parameters
     * @param interactiveMode
     *            When the NoraUi CLI goal is executed in interactive mode, it will prompt the user for all the
     *            previously listed parameters.
     *            When interactiveMode is false, the NoraUi CLI goal will use the values passed in from the command
     *            line.
     */
    private void removeModel(NoraUiCliFile noraUiCliFile, NoraUiCliParameters noraUiCliParameters) {
        if (noraUiCliParameters.getInteractiveMode()) {
            boolean applicationFinded = false;
            boolean modelFinded = false;
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName()) || noraUiCliParameters.getModelName() == null
                    || "".equals(noraUiCliParameters.getModelName())) {
                List<String> appList = model.getApplications(noraUiCliParameters.getRobotContext());
                if (!appList.isEmpty()) {
                    noraUiCliParameters.setApplicationName(askApplicationNumber(noraUiCliParameters.getInput(), appList));
                    applicationFinded = true;
                } else {
                    log.info("Your robot does not contain applications.");
                }

                List<String> modelList = model.getModels(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getRobotContext());
                if (!modelList.isEmpty()) {
                    log.info("Enter index model number:");
                    for (int i = 0; i < modelList.size(); i++) {
                        log.info(CLI_TAB, i + 1, modelList.get(i));
                    }
                    int modelCode = noraUiCliParameters.getInput().nextInt();
                    noraUiCliParameters.getInput().nextLine();
                    noraUiCliParameters.setModelName(modelList.get(modelCode - 1));
                    modelFinded = true;
                } else {
                    log.info("Your robot does not contain models.");
                }
            }
            if (applicationFinded && modelFinded) {
                model.remove(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        } else {
            if (noraUiCliParameters.getApplicationName() == null || "".equals(noraUiCliParameters.getApplicationName()) || noraUiCliParameters.getModelName() == null
                    || "".equals(noraUiCliParameters.getModelName())) {
                log.error("When you want to remove a model with interactiveMode is false, you need use -a and -m");
            } else {
                model.remove(noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName(), noraUiCliParameters.getRobotContext(), noraUiCliParameters.getVerbose());
            }
        }
        removeModelInCliFileFeature(noraUiCliFile, noraUiCliParameters.getApplicationName(), noraUiCliParameters.getModelName());
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
                log.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (data == null || "".equals(data)) {
                log.info("Enter data:");
                data = input.nextLine();
            }
            if (log.isInfoEnabled()) {
                log.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
                log.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
            }
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || data == null || "".equals(data)) {
                log.error("When you want to encrypt data with interactiveMode is false, you need use -d and -k");
            } else if (log.isInfoEnabled()) {
                log.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
                log.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
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
                log.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (description == null || "".equals(description)) {
                log.info("Enter data:");
                description = input.nextLine();
            }
            if (log.isInfoEnabled()) {
                log.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
                log.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
            }
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || description == null || "".equals(description)) {
                log.error("When you want to decrypt data with interactiveMode is false, you need use -d and -k");
            } else if (log.isInfoEnabled()) {
                log.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
                log.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
            }
        }
    }

    /**
     * CLI status feature display all datas from NoraUi CLI files (.noraui folder at the root of robot).
     * if the status is false, the element is filtered.
     * 
     * @param noraUiCliFile
     *            Object contain all data from CLI Files.
     */
    private void status(NoraUiCliFile noraUiCliFile) {
        List<NoraUiApplicationFile> applications = noraUiCliFile.getApplicationFiles().stream().filter(NoraUiApplicationFile::getStatus).collect(Collectors.toList());
        for (NoraUiApplicationFile a : applications) {
            log.info("Application: [{}]", a.getName());
            log.info(" - url: [{}]", a.getUrl());
            for (NoraUiModel m : a.getModels()) {
                log.info(" - model: [{}]", m.getName());
                for (NoraUiField field : m.getFields()) {
                    log.info("   - field: [{}]", field.getName());
                }
                for (NoraUiResult result : m.getResults()) {
                    log.info("   - result: [{}]", result.getName());
                }
            }
        }
        List<NoraUiScenarioFile> scenarios = noraUiCliFile.getScenarioFiles().stream().filter(NoraUiScenarioFile::getStatus).collect(Collectors.toList());
        ;
        for (NoraUiScenarioFile s : scenarios) {
            log.info("Scenario: [{}]", s.getName());
            log.info(" - description: [{}]", s.getDescription());
            log.info(" - application: [{}]", s.getApplication());
        }
    }

    /**
     * Display CLI footer.
     */
    private void displayFooter() {
        log.info("");
        log.info("NoraUi Command Line Interface finished with success.");
        log.info("");
    }

    /**
     * Display end CLI footer.
     */
    private void displayEndFooter() {
        log.info("");
        log.info("Exit NoraUi Command Line Interface with success.");
        log.info("");
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
