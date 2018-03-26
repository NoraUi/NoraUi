package com.github.noraui.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NoraUiCommandLineInterface {

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
     *            is list of args (-h, --verbose, --interactiveMode, -f, -s, -u, -d, -a, -m and -fi)
     */
    public void runCli(Class<?> context, String... args) {
        Scanner input = null;
        boolean runCli = true;
        boolean verbose = false;
        boolean interactiveMode = true;
        String feature = null;
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
        getPriorityParameters(args, verbose, interactiveMode);

        if (interactiveMode) {
            input = new Scanner(System.in);
        }
        do {
            featureCode = -1;
            if (!interactiveMode) {
                getParameters(args, feature, scenarioName, url, description, applicationName, modelName, fields, results);
                if (verbose) {
                    displayCommandLine(args);
                }
                runCli = false;
            } else {
                for (Map.Entry<String, String> f : features.entrySet()) {
                    if (f.getKey().equals(feature)) {
                        System.out.println("Do you want " + f.getValue().toLowerCase() + "? Y");
                        if ("y".equalsIgnoreCase(input.nextLine())) {
                            featureCode = Integer.parseInt(f.getKey());
                            break;
                        }
                    }
                }
            }

            if (featureCode == -1 && !interactiveMode) {
                System.err.println("When interactiveMode is false, you need use -f");
            } else {
                System.out.println("What do you want ?");
                for (Map.Entry<String, String> f : features.entrySet()) {
                    System.out.println("    " + f.getKey() + " => " + f.getValue());
                }
                featureCode = input.nextInt();
                input.nextLine();
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
        System.out.println("");
        System.out.println("  ███╗   ██╗ ██████╗ ██████╗  █████╗ ██╗   ██╗██╗      ██████╗██╗     ██╗ ");
        System.out.println("  ████╗  ██║██╔═══██╗██╔══██╗██╔══██╗██║   ██║██║     ██╔════╝██║     ██║ ");
        System.out.println("  ██╔██╗ ██║██║   ██║██████╔╝███████║██║   ██║██║     ██║     ██║     ██║ ");
        System.out.println("  ██║╚██╗██║██║   ██║██╔══██╗██╔══██║██║   ██║██║     ██║     ██║     ██║ ");
        System.out.println("  ██║ ╚████║╚██████╔╝██║  ██║██║  ██║╚██████╔╝██║     ╚██████╗███████╗██║ ");
        System.out.println("  ╚═╝  ╚═══╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝      ╚═════╝╚══════╝╚═╝ ");
        System.out.println("");
        System.out.println("  NoraUi Command Line Interface =>");
        System.out.println("");
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
            System.out.println("-h: Display this help");
            System.out.println("--verbose: Add debug informations in console.");
            System.out.println("-f: features 1 => add new application");
            for (Map.Entry<String, String> f : features.entrySet()) {
                System.out.println("             " + f.getKey() + " => " + f.getValue());
            }
            System.out.println("-s: Scenario Name");
            System.out.println("-u: Url");
            System.out.println("-d: Description");
            System.out.println("-a: Application Name");
            System.out.println("-m: Model Name");
            System.out.println("-fi: Field list of model");
        }
    }

    private void displayCommandLine(String[] args) {
        System.out.print("Command Line: ");
        for (String arg : args) {
            System.out.print(" " + arg);
        }
        System.out.println("");
    }

    private void getPriorityParameters(String[] args, boolean verbose, boolean interactiveMode) {
        for (int i = 0; i < args.length; i++) {
            if ("--verbose".equals(args[i])) {
                verbose = true;
            } else if ("--interactiveMode".equals(args[i])) {
                interactiveMode = Boolean.parseBoolean(args[i + 1]);
            }
        }
    }

    private void getParameters(String[] args, String feature, String scenarioName, String url, String description, String applicationName, String modelName, String fields, String results) {
        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i])) {
                feature = args[i + 1];
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
        if (interactiveMode && (applicationName == null || "".equals(applicationName) || url == null || "".equals(url))) {
            if (applicationName == null || "".equals(applicationName)) {
                System.out.println("Enter application name:");
                applicationName = input.nextLine();
            }
            if (url == null || "".equals(url)) {
                System.out.println("Enter url:");
                url = input.nextLine();
            }
            application.add(applicationName, url, context, verbose);
        } else {
            System.err.println("When interactiveMode is false, you need use -a and -u");
        }
    }

    private void addScenario(String applicationName, String scenarioName, String description, String robotName, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode && (scenarioName == null || "".equals(scenarioName) || description == null || "".equals(description) || applicationName == null || "".equals(applicationName))) {
            boolean applicationFinded = false;
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                if (appList.size() > 0) {
                    System.out.println("Enter index application number:");
                    for (int i = 0; i < appList.size(); i++) {
                        System.out.println("    " + (i + 1) + ") " + appList.get(i));
                    }
                    int appCode = input.nextInt();
                    input.nextLine();
                    applicationName = appList.get(appCode - 1);
                    applicationFinded = true;
                } else {
                    System.out.println("You must create an application first.");
                }
            }
            if (applicationFinded && (scenarioName == null || "".equals(scenarioName))) {
                System.out.println("Enter scenario name:");
                scenarioName = input.nextLine();
            }
            if (applicationFinded && (description == null || "".equals(description))) {
                System.out.println("Enter description:");
                description = input.nextLine();
            }
            if (applicationFinded) {
                scenario.add(scenarioName, description, applicationName, robotName, verbose);
            }
        } else {
            System.err.println("When interactiveMode is false, you need use -a, -s and -d");
        }
    }

    private void addModel(String applicationName, String modelName, String fields, String results, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode && (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName) || fields == null || "".equals(fields))) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                System.out.println("Enter index application number:");
                for (int i = 0; i < appList.size(); i++) {
                    System.out.println("    " + (i + 1) + ") " + appList.get(i));
                }
                int appCode = input.nextInt();
                input.nextLine();
                applicationName = appList.get(appCode - 1);
            }
            if (modelName == null || "".equals(modelName)) {
                System.out.println("Enter model name:");
                modelName = input.nextLine();
            }
            if (fields == null || "".equals(fields)) {
                System.out.println("Enter field list:");
                fields = input.nextLine();
            }
            if (results == null || "".equals(results)) {
                System.out.println("Enter result list:");
                results = input.nextLine();
                if ("".equals(results)) {
                    results = null;
                }
            }
            model.add(applicationName, modelName, fields, results, robotContext, verbose);
        } else {
            System.err.println("When interactiveMode is false, you need use -a, -m,  -fi and -re (optional)");
        }
    }

    private void removeApplication(String applicationName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode && (applicationName == null || "".equals(applicationName))) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = application.get();
                System.out.println("Enter index application number:");
                for (int i = 0; i < appList.size(); i++) {
                    System.out.println("    " + (i + 1) + ") " + appList.get(i));
                }
                int appCode = input.nextInt();
                input.nextLine();
                applicationName = appList.get(appCode - 1);
            }
            application.remove(applicationName, robotContext, verbose);
        } else {
            System.err.println("When interactiveMode is false, you need use -a");
        }
    }

    private void removeScenario(String scenarioName, String robotName, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode && (scenarioName == null || "".equals(scenarioName))) {
            if (scenarioName == null || "".equals(scenarioName)) {
                System.out.println("Enter scenario name:");
                scenarioName = input.nextLine();
            }
            scenario.remove(scenarioName, robotName, verbose);
        } else {
            System.err.println("When interactiveMode is false, you need use -s");
        }
    }

    private void removeModel(String applicationName, String modelName, Class<?> robotContext, boolean verbose, Scanner input, boolean interactiveMode) {
        if (interactiveMode && (applicationName == null || "".equals(applicationName) || modelName == null || "".equals(modelName))) {
            if (applicationName == null || "".equals(applicationName)) {
                List<String> appList = model.getApplications(robotContext);
                System.out.println("Enter index application number:");
                for (int i = 0; i < appList.size(); i++) {
                    System.out.println("    " + (i + 1) + ") " + appList.get(i));
                }
                int appCode = input.nextInt();
                input.nextLine();
                applicationName = appList.get(appCode - 1);
            }
            if (modelName == null || "".equals(modelName)) {
                List<String> modelList = model.getModels(applicationName, robotContext);
                System.out.println("Enter index model number:");
                for (int i = 0; i < modelList.size(); i++) {
                    System.out.println("    " + (i + 1) + ") " + modelList.get(i));
                }
                int modelCode = input.nextInt();
                input.nextLine();
                modelName = modelList.get(modelCode - 1);
            }
            model.remove(applicationName, modelName, robotContext, verbose);
        } else {
            System.err.println("When interactiveMode is false, you need use -a and -m");
        }
    }

    private void displayFooter() {
        System.out.println("");
        System.out.println("NoraUi Command Line Interface finished with success.");
        System.out.println("");
    }

    private void displayEndFooter() {
        System.out.println("");
        System.out.println("Exit NoraUi Command Line Interface with success.");
        System.out.println("");
    }
}
