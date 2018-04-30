package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;
import static com.github.noraui.utils.Constants.CLI_FILES_DIR;
import static com.github.noraui.utils.Messages.CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import com.github.noraui.cli.model.NoraUiModel;
import com.github.noraui.cli.model.NoraUiScenarioFile;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.service.CryptoService;
import com.github.noraui.service.impl.CryptoServiceImpl;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class NoraUiCommandLineInterface {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NoraUiCommandLineInterface.class);

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
     * @param args
     *            is list of args (-h, --verbose, --update, -interactiveMode, -f, -s, -u, -d, -k, -a, -m, -fi and -re)
     * @throws TechnicalException
     */
    public void runCli(Class<?> context, String... args) throws TechnicalException {
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
                            logger.info("Do you want {}? Y", f.getValue().toLowerCase());
                            if ("y".equalsIgnoreCase(input.nextLine())) {
                                featureCode = Integer.parseInt(f.getKey());
                                break;
                            }
                        }
                    }
                }

                if (featureCode == -1 && !interactiveMode) {
                    logger.error("When interactiveMode is false, you need use -f");
                } else {
                    if (featureCode == -1) {
                        logger.info("What do you want ?");
                        for (Map.Entry<String, String> f : features.entrySet()) {
                            logger.info("    {} => {}", f.getKey(), f.getValue());
                        }
                        featureCode = input.nextInt();
                        input.nextLine();
                    }
                    if (featureCode > 0) {
                        noraUiCliFile = runFeature(noraUiCliFile, featureCode, applicationName, scenarioName, modelName, url, description, fields, results, cryptoKey, context, verbose, input,
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
        logger.info("");
        logger.info("  ███╗   ██╗ ██████╗ ██████╗  █████╗ ██╗   ██╗██╗      ██████╗██╗     ██╗ ");
        logger.info("  ████╗  ██║██╔═══██╗██╔══██╗██╔══██╗██║   ██║██║     ██╔════╝██║     ██║ ");
        logger.info("  ██╔██╗ ██║██║   ██║██████╔╝███████║██║   ██║██║     ██║     ██║     ██║ ");
        logger.info("  ██║╚██╗██║██║   ██║██╔══██╗██╔══██║██║   ██║██║     ██║     ██║     ██║ ");
        logger.info("  ██║ ╚████║╚██████╔╝██║  ██║██║  ██║╚██████╔╝██║     ╚██████╗███████╗██║ ");
        logger.info("  ╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝      ╚═════╝╚══════╝╚═╝ ");
        logger.info("");
        logger.info("  NoraUi Command Line Interface =>");
        logger.info("");
    }

    /**
     * @return
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
        features.put("0", "exit NoraUi CLI");
        return features;
    }

    /**
     * @param args
     * @param features
     */
    private void displayHelp(String[] args, Map<String, String> features) {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("-help"))) {
            logger.info("-h: Display this help");
            logger.info("--verbose: Add debug informations in console.");
            logger.info("-f: features 1 => add new application");
            for (Map.Entry<String, String> f : features.entrySet()) {
                logger.info("             {} => {}", f.getKey(), f.getValue());
            }
            logger.info("-s: Scenario Name");
            logger.info("-u: Url");
            logger.info("-d: Description");
            logger.info("-k: Crypto key");
            logger.info("-a: Application Name");
            logger.info("-m: Model Name");
            logger.info("-fi: Field list of model");
            logger.info("-re: Result list of model");
        }
    }

    /**
     * @param args
     */
    private void displayCommandLine(String[] args) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("Command Line: ");
        for (String arg : args) {
            cmd.append(" ").append(arg);
        }
        logger.info(cmd.toString());
    }

    /**
     * @param verbose
     * @return
     */
    protected NoraUiCliFile readNoraUiCliFiles(boolean verbose) {
        NoraUiCliFile result = new NoraUiCliFile();
        List<NoraUiApplicationFile> noraUiApplicationFiles = new ArrayList<>();
        List<NoraUiScenarioFile> noraUiScenarioFiles = new ArrayList<>();
        Gson gson = new Gson();
        String[] applications = new File(CLI_FILES_DIR + File.separator + "applications").list();
        String[] scenarios = new File(CLI_FILES_DIR + File.separator + "scenarios").list();
        if (applications != null) {
            for (String application : applications) {
                if (verbose) {
                    logger.info("CLI File [{}] found.", application);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + "applications" + File.separator + application))) {
                    NoraUiApplicationFile noraUiApplicationFile = gson.fromJson(bufferedReader, NoraUiApplicationFile.class);
                    noraUiApplicationFiles.add(noraUiApplicationFile);
                } catch (IOException e) {
                    logger.error("noraUiApplicationFiles IOException: {}", e.getMessage(), e);
                }
            }
        }
        if (scenarios != null) {
            for (String scenario : scenarios) {
                if (verbose) {
                    logger.info("CLI File [{}] found.", scenario);
                }
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(CLI_FILES_DIR + File.separator + "scenarios" + File.separator + scenario))) {
                    NoraUiScenarioFile noraUiScenarioFile = gson.fromJson(bufferedReader, NoraUiScenarioFile.class);
                    noraUiScenarioFiles.add(noraUiScenarioFile);
                } catch (IOException e) {
                    logger.error("noraUiScenarioFiles IOException: {}", e.getMessage(), e);
                }
            }
        }
        result.setApplicationFiles(noraUiApplicationFiles);
        result.setScenarioFiles(noraUiScenarioFiles);
        return result;
    }

    /**
     * @param noraUiCliFile
     */
    protected void writeNoraUiCliFiles(NoraUiCliFile noraUiCliFile, boolean verbose) {
        Gson gson = new Gson();
        for (NoraUiApplicationFile noraUiApplicationFile : noraUiCliFile.getApplicationFiles()) {
            try {
                FileUtils.forceMkdir(new File(CLI_FILES_DIR + File.separator + "applications"));
                File applicationFile = new File(CLI_FILES_DIR + File.separator + "applications" + File.separator + noraUiApplicationFile.getName() + ".json");
                if (!applicationFile.exists()) {
                    Files.asCharSink(applicationFile, Charsets.UTF_8).write(gson.toJson(noraUiApplicationFile));
                    if (verbose) {
                        logger.info("File [{}.json] created with success.", noraUiApplicationFile.getName());
                    }
                } else {
                    if (verbose) {
                        logger.info("File [{}.json] already exist.", noraUiApplicationFile.getName());
                    }
                }
            } catch (Exception e) {
                logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
            }
            try (FileWriter fw = new FileWriter(CLI_FILES_DIR + File.separator + "applications" + File.separator + noraUiApplicationFile.getName() + ".json")) {
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(gson.toJson(noraUiApplicationFile));
                bw.flush();
                bw.close();
            } catch (IOException e) {
                logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
            }
        }
        for (NoraUiScenarioFile noraUiScenarioFile : noraUiCliFile.getScenarioFiles()) {
            try {
                FileUtils.forceMkdir(new File(CLI_FILES_DIR + File.separator + "scenarios"));
                File scenarioFile = new File(CLI_FILES_DIR + File.separator + "scenarios" + File.separator + noraUiScenarioFile.getName() + ".json");
                if (!scenarioFile.exists()) {
                    Files.asCharSink(scenarioFile, Charsets.UTF_8).write(gson.toJson(noraUiScenarioFile));
                    if (verbose) {
                        logger.info("File [{}.json] created with success.", noraUiScenarioFile.getName());
                    }
                } else {
                    if (verbose) {
                        logger.info("File [{}.json] already exist.", noraUiScenarioFile.getName());
                    }
                }
            } catch (Exception e) {
                logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
            }
        }
    }

    /**
     * @param noraUiCliFile
     */
    private void updateRobotFromNoraUiCliFiles(NoraUiCliFile noraUiCliFile, Class<?> robotContext, boolean verbose) {
        logger.info("updateRobotFromNoraUiCliFiles");
        for (NoraUiApplicationFile noraUiApplicationFile : noraUiCliFile.getApplicationFiles()) {
            addApplication(null, noraUiApplicationFile.getName(), noraUiApplicationFile.getUrl(), robotContext, verbose, null, false);
            for (NoraUiModel noraUiModel : noraUiApplicationFile.getModels()) {
                addModel(null, noraUiApplicationFile.getName(), noraUiModel.getName(), noraUiModel.getFieldsString(), noraUiModel.getResultsString(), robotContext, verbose, null, false);
            }
        }
        for (NoraUiScenarioFile noraUiScenarioFile : noraUiCliFile.getScenarioFiles()) {
            addScenario(null, noraUiScenarioFile.getApplication(), noraUiScenarioFile.getName(), noraUiScenarioFile.getDescription(), robotContext.getSimpleName().replaceAll("Context", ""), verbose,
                    null, false);
        }

    }

    /**
     * @param noraUiCliFile
     * @param featureCode
     * @param applicationName
     * @param scenarioName
     * @param modelName
     * @param url
     * @param description
     * @param fields
     * @param results
     * @param cryptoKey
     * @param robotContext
     * @param verbose
     * @param input
     * @param interactiveMode
     * @return
     * @throws TechnicalException
     */
    private NoraUiCliFile runFeature(NoraUiCliFile noraUiCliFile, int featureCode, String applicationName, String scenarioName, String modelName, String url, String description, String fields,
            String results, String cryptoKey, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (featureCode == 1) {
            noraUiCliFile = addApplication(noraUiCliFile, applicationName, url, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 2) {
            noraUiCliFile = addScenario(noraUiCliFile, applicationName, scenarioName, description, robotContext.getSimpleName().replaceAll("Context", ""), verbose, input, interactiveMode);
        } else if (featureCode == 3) {
            noraUiCliFile = addModel(noraUiCliFile, applicationName, modelName, fields, results, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 4) {
            noraUiCliFile = removeApplication(noraUiCliFile, applicationName, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 5) {
            noraUiCliFile = removeScenario(noraUiCliFile, scenarioName, robotContext.getSimpleName().replaceAll("Context", ""), verbose, input, interactiveMode);
        } else if (featureCode == 6) {
            noraUiCliFile = removeModel(noraUiCliFile, applicationName, modelName, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 7) {
            encrypt(cryptoKey, description, input, interactiveMode);
        } else if (featureCode == 8) {
            decrypt(cryptoKey, description, input, interactiveMode);
        }
        return noraUiCliFile;
    }

    /**
     * @param applicationName
     * @param url
     * @param context
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile addApplication(NoraUiCliFile noraUiCliFile, String applicationName, String url, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                logger.info("Enter application name:");
                applicationName = input.nextLine();
            }
            if (url == null || "".equals(url)) {
                logger.info("Enter url:");
                url = input.nextLine();
            }
            application.add(applicationName, url, robotContext, verbose);
        } else {
            if (applicationName == null || "".equals(applicationName) || url == null || "".equals(url)) {
                logger.error("When you want to add an application with interactiveMode is false, you need use -a and -u");
            } else {
                application.add(applicationName, url, robotContext, verbose);
            }
        }
        NoraUiApplicationFile noraUiApplicationFile = new NoraUiApplicationFile();
        noraUiApplicationFile.setName(applicationName);
        noraUiApplicationFile.setUrl(url);
        if (noraUiCliFile != null) {
            List<NoraUiApplicationFile> r = noraUiCliFile.addApplication(noraUiApplicationFile);
            noraUiCliFile.setApplicationFiles(r);
            return noraUiCliFile;
        }
        return null;
    }

    /**
     * @param applicationName
     * @param scenarioName
     * @param description
     * @param robotName
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile addScenario(NoraUiCliFile noraUiCliFile, String applicationName, String scenarioName, String description, String robotName, boolean verbose, Scanner input,
            boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                    applicationFinded = true;
                } else {
                    logger.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (applicationFinded && (scenarioName == null || "".equals(scenarioName))) {
                logger.info("Enter scenario name:");
                scenarioName = input.nextLine();
            }
            if (applicationFinded && (description == null || "".equals(description))) {
                logger.info("Enter description:");
                description = input.nextLine();
            }
            if (applicationFinded) {
                scenario.add(scenarioName, description, applicationName, robotName, verbose);
            }
        } else {
            if (scenarioName == null || "".equals(scenarioName) || description == null || "".equals(description) || applicationName == null || "".equals(applicationName)) {
                logger.error("When you want to add a scenario with interactiveMode is false, you need use -a, -s and -d");
            } else {
                if (isApplicationFound(applicationName)) {
                    scenario.add(scenarioName, description, applicationName, robotName, verbose);
                }
            }
        }
        if (noraUiCliFile != null) {
            NoraUiScenarioFile noraUiScenarioFile = new NoraUiScenarioFile();
            noraUiScenarioFile.setName(scenarioName);
            noraUiScenarioFile.setDescription(description);
            noraUiScenarioFile.setApplication(applicationName);
            List<NoraUiScenarioFile> r = noraUiCliFile.addScenario(noraUiScenarioFile);
            noraUiCliFile.setScenarioFiles(r);
            return noraUiCliFile;
        }
        return null;
    }

    /**
     * @param input
     * @param appList
     * @return
     */
    private String askApplicationNumber(Scanner input, List<String> appList) {
        String applicationName;
        logger.info("Enter index application number:");
        for (int i = 0; i < appList.size(); i++) {
            logger.info("    {}) {}", i + 1, appList.get(i));
        }
        int appCode = input.nextInt();
        input.nextLine();
        applicationName = appList.get(appCode - 1);
        return applicationName;
    }

    /**
     * @param input
     * @param scenarioList
     * @return
     */
    private String askScenarioNumber(Scanner input, List<String> scenarioList) {
        String scenarioName;
        logger.info("Enter index scenario number:");
        for (int i = 0; i < scenarioList.size(); i++) {
            logger.info("    {}) {}", i + 1, scenarioList.get(i));
        }
        int scenarioCode = input.nextInt();
        input.nextLine();
        scenarioName = scenarioList.get(scenarioCode - 1);
        return scenarioName;
    }

    /**
     * @param applicationName
     * @param modelName
     * @param fields
     * @param results
     * @param robotContext
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile addModel(NoraUiCliFile noraUiCliFile, String applicationName, String modelName, String fields, String results, Class<?> robotContext, boolean verbose, Scanner input,
            boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                    applicationFinded = true;
                } else {
                    logger.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
                }
            }
            if (applicationFinded && (modelName == null || "".equals(modelName))) {
                logger.info("Enter model name:");
                modelName = input.nextLine();
            }
            if (applicationFinded && (fields == null || "".equals(fields))) {
                logger.info("Enter field list:");
                fields = input.nextLine();
            }
            if (applicationFinded && (results == null || "".equals(results))) {
                logger.info("Enter result list:");
                results = input.nextLine();
                if ("".equals(results)) {
                    results = null;
                }
            }
            if (applicationFinded) {
                model.add(applicationName, modelName, fields, results, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName) || fields == null || "".equals(fields)) {
                logger.error("When you want to add a model with interactiveMode is false, you need use -a, -m, -fi and -re (optional)");
            } else {
                if (isApplicationFound(applicationName)) {
                    model.add(applicationName, modelName, fields, results, robotContext, verbose);
                }
            }
        }

        if (noraUiCliFile != null) {
            NoraUiModel noraUiModel = new NoraUiModel();
            noraUiModel.setName(modelName);
            noraUiModel.setFields(fields);
            noraUiModel.setResults(results);
            List<NoraUiApplicationFile> r = noraUiCliFile.addModel(applicationName, noraUiModel);
            noraUiCliFile.setApplicationFiles(r);
            return noraUiCliFile;
        }
        return null;
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
                logger.info("Application [{}] do not exist. You must create an application named [{}] first.", applicationName, applicationName);
            }
        } else {
            logger.info(CLI_YOU_MUST_CREATE_AN_APPLICATION_FIRST);
        }
        return applicationFinded;
    }

    /**
     * @param applicationName
     * @param robotContext
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile removeApplication(NoraUiCliFile noraUiCliFile, String applicationName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                    applicationFinded = true;
                } else {
                    logger.info("Your robot does not contain applications.");
                }
            }
            if (applicationFinded) {
                application.remove(applicationName, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName)) {
                logger.error("When you want to remove an application with interactiveMode is false, you need use -a");
            } else {
                application.remove(applicationName, robotContext, verbose);
            }
        }
        if (applicationName != null && !"".equals(applicationName)) {
            List<NoraUiApplicationFile> r = noraUiCliFile.removeApplication(applicationName);
            noraUiCliFile.setApplicationFiles(r);
        }
        return noraUiCliFile;
    }

    /**
     * @param scenarioName
     * @param robotName
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile removeScenario(NoraUiCliFile noraUiCliFile, String scenarioName, String robotName, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean scenarioFinded = false;
            if (scenarioName == null || "".equals(scenarioName)) {
                List<String> scenarioList = scenario.get();
                if (!scenarioList.isEmpty()) {
                    scenarioName = askScenarioNumber(input, scenarioList);
                    scenarioFinded = true;
                } else {
                    logger.info("Your robot does not contain scenarios.");
                }
            }
            if (scenarioFinded) {
                scenario.remove(scenarioName, robotName, verbose);
            }
        } else {
            if (scenarioName == null || "".equals(scenarioName)) {
                logger.error("When you want to remove a scenario with interactiveMode is false, you need use -s");
            } else {
                scenario.remove(scenarioName, robotName, verbose);
            }
        }
        if (scenarioName != null && !"".equals(scenarioName)) {
            List<NoraUiScenarioFile> r = noraUiCliFile.removeScenario(scenarioName);
            noraUiCliFile.setScenarioFiles(r);
        }
        return noraUiCliFile;
    }

    /**
     * @param applicationName
     * @param modelName
     * @param robotContext
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private NoraUiCliFile removeModel(NoraUiCliFile noraUiCliFile, String applicationName, String modelName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            boolean modelFinded = false;
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName)) {
                List<String> appList = model.getApplications(robotContext);
                if (!appList.isEmpty()) {
                    applicationName = askApplicationNumber(input, appList);
                    applicationFinded = true;
                } else {
                    logger.info("Your robot does not contain applications.");
                }

                List<String> modelList = model.getModels(applicationName, robotContext);
                if (!modelList.isEmpty()) {
                    logger.info("Enter index model number:");
                    for (int i = 0; i < modelList.size(); i++) {
                        logger.info("    {}) {}", i + 1, modelList.get(i));
                    }
                    int modelCode = input.nextInt();
                    input.nextLine();
                    modelName = modelList.get(modelCode - 1);
                    modelFinded = true;
                } else {
                    logger.info("Your robot does not contain models.");
                }
            }
            if (applicationFinded && modelFinded) {
                model.remove(applicationName, modelName, robotContext, verbose);
            }
        } else {
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName)) {
                logger.error("When you want to remove a model with interactiveMode is false, you need use -a and -m");
            } else {
                model.remove(applicationName, modelName, robotContext, verbose);
            }
        }
        if (applicationName != null && !"".equals(applicationName)) {
            List<NoraUiApplicationFile> r = noraUiCliFile.removeModel(applicationName, modelName);
            noraUiCliFile.setApplicationFiles(r);
        }
        return noraUiCliFile;
    }

    /**
     * @param cryptoKey
     * @param description
     * @param input
     * @param interactiveMode
     * @throws TechnicalException
     */
    private void encrypt(String cryptoKey, String data, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (interactiveMode) {
            if (cryptoKey == null || "".equals(cryptoKey)) {
                logger.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (data == null || "".equals(data)) {
                logger.info("Enter data:");
                data = input.nextLine();
            }
            logger.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
            logger.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || data == null || "".equals(data)) {
                logger.error("When you want to encrypt data with interactiveMode is false, you need use -d and -k");
            } else {
                logger.info("Encrypt a data [{}] with this crypto key: [{}]", data, cryptoKey);
                logger.info("Encrypted value is {}", cryptoService.encrypt(cryptoKey, data));
            }
        }

    }

    /**
     * @param cryptoKey
     * @param description
     * @param input
     * @param interactiveMode
     * @throws TechnicalException
     */
    private void decrypt(String cryptoKey, String description, Scanner input, boolean interactiveMode) throws TechnicalException {
        if (interactiveMode) {
            if (cryptoKey == null || "".equals(cryptoKey)) {
                logger.info("Enter crypto key:");
                cryptoKey = input.nextLine();
            }
            if (description == null || "".equals(description)) {
                logger.info("Enter data:");
                description = input.nextLine();
            }
            logger.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
            logger.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
        } else {
            if (cryptoKey == null || "".equals(cryptoKey) || description == null || "".equals(description)) {
                logger.error("When you want to decrypt data with interactiveMode is false, you need use -d and -k");
            } else {
                logger.info("Decrypt a data [{}] with this crypto key: [{}]", description, cryptoKey);
                logger.info("Decrypted value is {}", cryptoService.decrypt(cryptoKey, description));
            }
        }
    }

    /**
     * 
     */
    private void displayFooter() {
        logger.info("");
        logger.info("NoraUi Command Line Interface finished with success.");
        logger.info("");
    }

    /**
     * 
     */
    private void displayEndFooter() {
        logger.info("");
        logger.info("Exit NoraUi Command Line Interface with success.");
        logger.info("");
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
