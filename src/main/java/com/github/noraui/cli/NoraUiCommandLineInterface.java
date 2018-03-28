package com.github.noraui.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoraUiCommandLineInterface {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(NoraUiCommandLineInterface.class);

    Application application;
    Scenario scenario;
    Model model;

    public NoraUiCommandLineInterface() {
        application = new Application();
        scenario = new Scenario();
        model = new Model();
    }

    /**
     * @param context
     *            is generated robot context class.
     * @param args
     *            is list of args (-h, --verbose, -interactiveMode, -f, -s, -u, -d, -a, -m, -fi and -re)
     */
    public void runCli(Class<?> context, String... args) {
        Scanner input = null;
        boolean runCli = true;
        boolean verbose = false;
        boolean interactiveMode = true;
        int featureCode = -1;
        String applicationName = null;
        String scenarioName = null;
        String modelName = null;
        String url = null;
        String description = null;
        String fields = null;
        String results = null;

        Map<String, String> features = getFeatures();

        displaySplashScreen();
        displayHelp(args, features);

        for (int i = 0; i < args.length; i++) {
            if ("--verbose".equals(args[i])) {
                verbose = true;
            } else if ("-interactiveMode".equals(args[i])) {
                interactiveMode = Boolean.parseBoolean(args[i + 1]);
            }
        }

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
                        logger.info("Do you want " + f.getValue().toLowerCase() + "? Y");
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
                        logger.info("    " + f.getKey() + " => " + f.getValue());
                    }
                    featureCode = input.nextInt();
                    input.nextLine();
                }
                if (featureCode > 0) {
                    runFeature(featureCode, applicationName, scenarioName, modelName, url, description, fields, results, context, verbose, input, interactiveMode);
                    displayFooter();
                } else {
                    runCli = false;
                }
            }
        } while (runCli);
        if (interactiveMode) {
            input.close();
        }
        displayEndFooter();
    }

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

    private Map<String, String> getFeatures() {
        Map<String, String> features = new HashMap<>();
        features.put("1", "add new application");
        features.put("2", "add new scenario");
        features.put("3", "add new model");
        features.put("4", "remove application");
        features.put("5", "remove scenario");
        features.put("6", "remove model");
        features.put("0", "exit NoraUi CLI");
        return features;
    }

    private void displayHelp(String[] args, Map<String, String> features) {
        if (args.length == 1 && (args[0].equals("-h") || args[0].equals("-help"))) {
            logger.info("-h: Display this help");
            logger.info("--verbose: Add debug informations in console.");
            logger.info("-f: features 1 => add new application");
            for (Map.Entry<String, String> f : features.entrySet()) {
                logger.info("             " + f.getKey() + " => " + f.getValue());
            }
            logger.info("-s: Scenario Name");
            logger.info("-u: Url");
            logger.info("-d: Description");
            logger.info("-a: Application Name");
            logger.info("-m: Model Name");
            logger.info("-fi: Field list of model");
            logger.info("-re: Result list of model");
        }
    }

    private void displayCommandLine(String[] args) {
        System.out.print("Command Line: ");
        for (String arg : args) {
            System.out.print(" " + arg);
        }
        logger.info("");
    }

    private void runFeature(int featureCode, String applicationName, String scenarioName, String modelName, String url, String description, String fields, String results, Class<?> robotContext,
            boolean verbose, Scanner input, boolean interactiveMode) {
        if (featureCode == 1) {
            addApplication(applicationName, url, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 2) {
            addScenario(applicationName, scenarioName, description, robotContext.getSimpleName().replaceAll("Context", ""), verbose, input, interactiveMode);
        } else if (featureCode == 3) {
            addModel(applicationName, modelName, fields, results, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 4) {
            removeApplication(applicationName, robotContext, verbose, input, interactiveMode);
        } else if (featureCode == 5) {
            removeScenario(scenarioName, robotContext.getSimpleName().replaceAll("Context", ""), verbose, input, interactiveMode);
        } else if (featureCode == 6) {
            removeModel(applicationName, modelName, robotContext, verbose, input, interactiveMode);
        }
    }

    private void addApplication(String applicationName, String url, Class<?> context, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                logger.info("Enter application name:");
                applicationName = input.nextLine();
            }
            if (url == null || "".equals(url)) {
                logger.info("Enter url:");
                url = input.nextLine();
            }
            application.add(applicationName, url, context, verbose);
        } else {
            if (applicationName == null || "".equals(applicationName) || url == null || "".equals(url)) {
                logger.error("When you want to add an application with interactiveMode is false, you need use -a and -u");
            } else {
                application.add(applicationName, url, context, verbose);
            }
        }
    }

    private void addScenario(String applicationName, String scenarioName, String description, String robotName, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (appList.size() > 0) {
                    logger.info("Enter index application number:");
                    for (int i = 0; i < appList.size(); i++) {
                        logger.info("    " + (i + 1) + ") " + appList.get(i));
                    }
                    int appCode = input.nextInt();
                    input.nextLine();
                    applicationName = appList.get(appCode - 1);
                    applicationFinded = true;
                } else {
                    logger.info("You must create an application first.");
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
    }

    private void addModel(String applicationName, String modelName, String fields, String results, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (appList.size() > 0) {
                    logger.info("Enter index application number:");
                    for (int i = 0; i < appList.size(); i++) {
                        logger.info("    " + (i + 1) + ") " + appList.get(i));
                    }
                    int appCode = input.nextInt();
                    input.nextLine();
                    applicationName = appList.get(appCode - 1);
                    applicationFinded = true;
                } else {
                    logger.info("You must create an application first.");
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
        if (appList.size() > 0) {
            if (appList.contains(applicationName)) {
                applicationFinded = true;
            } else {
                logger.info("Application [{}] do not exist. You must create an application named [{}] first.", applicationName, applicationName);
            }
        } else {
            logger.info("You must create an application first.");
        }
        return applicationFinded;
    }

    private void removeApplication(String applicationName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                logger.info("Enter index application number:");
                for (int i = 0; i < appList.size(); i++) {
                    logger.info("    " + (i + 1) + ") " + appList.get(i));
                }
                int appCode = input.nextInt();
                input.nextLine();
                applicationName = appList.get(appCode - 1);
            }
            application.remove(applicationName, robotContext, verbose);
        } else {
            if (applicationName == null || "".equals(applicationName)) {
                logger.error("When you want to remove an application with interactiveMode is false, you need use -a");
            } else {
                application.remove(applicationName, robotContext, verbose);
            }
        }
    }

    /**
     * @param scenarioName
     * @param robotName
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private void removeScenario(String scenarioName, String robotName, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (scenarioName == null || "".equals(scenarioName)) {
                logger.info("Enter scenario name:");
                scenarioName = input.nextLine();
            }
            scenario.remove(scenarioName, robotName, verbose);
        } else {
            if (scenarioName == null || "".equals(scenarioName)) {
                logger.error("When you want to remove a scenario with interactiveMode is false, you need use -s");
            } else {
                scenario.remove(scenarioName, robotName, verbose);
            }
        }
    }

    /**
     * @param applicationName
     * @param modelName
     * @param robotContext
     * @param verbose
     * @param input
     * @param interactiveMode
     */
    private void removeModel(String applicationName, String modelName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = model.getApplications(robotContext);
                logger.info("Enter index application number:");
                for (int i = 0; i < appList.size(); i++) {
                    logger.info("    " + (i + 1) + ") " + appList.get(i));
                }
                int appCode = input.nextInt();
                input.nextLine();
                applicationName = appList.get(appCode - 1);
            }
            if (modelName == null || "".equals(modelName)) {
                List<String> modelList = model.getModels(applicationName, robotContext);
                logger.info("Enter index model number:");
                for (int i = 0; i < modelList.size(); i++) {
                    logger.info("    " + (i + 1) + ") " + modelList.get(i));
                }
                int modelCode = input.nextInt();
                input.nextLine();
                modelName = modelList.get(modelCode - 1);
            }
            model.remove(applicationName, modelName, robotContext, verbose);
        } else {
            if (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName)) {
                logger.error("When you want to remove a model with interactiveMode is false, you need use -a and -m");
            } else {
                model.remove(applicationName, modelName, robotContext, verbose);
            }
        }
    }

    private void displayFooter() {
        logger.info("");
        logger.info("NoraUi Command Line Interface finished with success.");
        logger.info("");
    }

    private void displayEndFooter() {
        logger.info("");
        logger.info("Exit NoraUi Command Line Interface with success.");
        logger.info("");
    }

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
