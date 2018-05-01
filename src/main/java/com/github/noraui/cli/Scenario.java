/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import static com.github.noraui.exception.TechnicalException.TECHNICAL_IO_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Scenario extends AbstractNoraUiCli {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Scenario.class);

    private String mainPath;

    public Scenario() {
        this.mainPath = "src" + File.separator + "main";
    }

    protected Scenario(String mainPath) {
        this.mainPath = mainPath;
    }

    /**
     * @return a list of available scenarios (name).
     */
    public List<String> get() {
        List<String> scenarios = new ArrayList<>();
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + "scenarios.properties";
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith("#") && !"".equals(line)) {
                    String[] l = line.split("=");
                    scenarios.add(l[0]);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        return scenarios;
    }

    /**
     * Add new scenario to your robot.
     * Sample if you add google: -f 2 -s loginSample -d "Scenario that sample." -a google --verbose
     * 
     * @param scenarioName
     * @param description
     * @param applicationName
     * @param noraRobotName
     * @param verbose
     */
    public void add(String scenarioName, String description, String applicationName, String noraRobotName, boolean verbose) {
        logger.info("Add a new scenario named [{}] on [{}] application with this description: [{}]", scenarioName, applicationName, description);
        addScenarioInData(scenarioName, noraRobotName, verbose);
        addScenarioInEnvPropertiesFile(scenarioName, verbose);
        addScenarioFeature(scenarioName, description, applicationName, verbose);
    }

    /**
     * Remove old scenario to your robot.
     * Sample if you add google: -f 3 -s loginSample -d "Scenario that sample." -a google --verbose
     * 
     * @param scenarioName
     * @param noraRobotName
     * @param verbose
     */
    public void remove(String scenarioName, String noraRobotName, boolean verbose) {
        logger.info("Remove a scenario named [{}].", scenarioName);
        removeScenarioInData(scenarioName, noraRobotName, verbose);
        removeScenarioInEnvPropertiesFile(scenarioName, verbose);
        removeScenarioFeature(scenarioName, verbose);
    }

    private void removeScenarioInData(String scenarioName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + noraRobotName + ".properties";

        String dataProviderIn = getDataProvider("in", propertiesfilePath);
        logger.info("dataProvider.in.type is [{}]", dataProviderIn);
        removeScenarioInData("in", scenarioName, dataProviderIn, verbose);

        String dataProviderOut = getDataProvider("out", propertiesfilePath);
        logger.info("dataProvider.out.type is [{}]", dataProviderOut);
        removeScenarioInData("out", scenarioName, dataProviderOut, verbose);
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void addScenarioInData(String scenarioName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + noraRobotName + ".properties";

        String dataProviderIn = getDataProvider("in", propertiesfilePath);
        logger.info("dataProvider.in.type is [{}]", dataProviderIn);
        addScenarioInData("in", scenarioName, dataProviderIn, verbose);

        String dataProviderOut = getDataProvider("out", propertiesfilePath);
        logger.info("dataProvider.out.type is [{}]", dataProviderOut);
        addScenarioInData("out", scenarioName, dataProviderOut, verbose);
    }

    /**
     * @param type
     * @param scenarioName
     * @param dataProviderIn
     */
    private void addScenarioInData(String type, String scenarioName, String dataProvider, boolean verbose) {
        if ("CSV".equals(dataProvider)) {
            String csvPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".csv";
            File newCsvfile = new File(csvPath);
            if (!newCsvfile.exists()) {
                addCsvFile(newCsvfile);
            }
        } else if ("DB".equals(dataProvider)) {
            String excelPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".sql";
            File sqlFile = new File(excelPath);
            if (!sqlFile.exists()) {
                addSqlFile(scenarioName, sqlFile);
            }
        } else if ("EXCEL".equals(dataProvider)) {
            String excelPath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".xlsx";
            File newExcelFile = new File(excelPath);
            if (!newExcelFile.exists()) {
                addXlsxFile(scenarioName, excelPath);
            }
        } else if (verbose) {
            logger.info("CLI do not add your data provider [{}]. CLI add only CSV, DB and EXCEL.", dataProvider);
        }
    }

    /**
     * @param type
     * @param scenarioName
     * @param dataProviderIn
     */
    private void removeScenarioInData(String type, String scenarioName, String dataProvider, boolean verbose) {
        String datafilePath = "";
        if ("CSV".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".csv";
        } else if ("DB".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".sql";
        } else if ("EXCEL".equals(dataProvider)) {
            datafilePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".xlsx";
        }
        if (!"".equals(datafilePath)) {
            try {
                FileUtils.forceDelete(new File(datafilePath));
                if (verbose) {
                    logger.info("{} removed with success.", datafilePath);
                }
            } catch (IOException e) {
                logger.debug("{} not revove because do not exist.", datafilePath);
            }
        } else {
            if (verbose) {
                logger.info("CLI do not remove your data provider [{}]. CLI remove only CSV, DB and EXCEL.", dataProvider);
            }
        }

    }

    /**
     * @param scenarioName
     * @param excelPath
     */
    private void addXlsxFile(String scenarioName, String excelPath) {
        try (FileOutputStream outputStream = new FileOutputStream(excelPath); XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("NoraUi-" + scenarioName);
            Object[][] datatypes = { { "user", "password", "Result" }, { "user1", "password1" }, { "user2", "password2" } };
            int rowNum = 0;
            for (Object[] datatype : datatypes) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (Object field : datatype) {
                    Cell cell = row.createCell(colNum++);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }
                }
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            logger.error("IOException {}", e.getMessage(), e);
        }
    }

    /**
     * @param scenarioName
     * @param sqlFile
     */
    private void addSqlFile(String scenarioName, File sqlFile) {
        StringBuilder sb = new StringBuilder();
        sb.append("(select t.user as \"user\", t.password1 as \"password1\", '' as Result from " + scenarioName + " t)").append(System.lineSeparator());
        sb.append("ORDER BY \"author\"").append(System.lineSeparator());
        try {
            Files.asCharSink(sqlFile, Charsets.UTF_8).write(sb.toString());
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param newCsvfile
     */
    private void addCsvFile(File newCsvfile) {
        StringBuilder sb = new StringBuilder();
        sb.append("user;password;Result").append(System.lineSeparator());
        sb.append("user1;password1;").append(System.lineSeparator());
        sb.append("user2;password2;").append(System.lineSeparator());
        try {
            Files.asCharSink(newCsvfile, Charsets.UTF_8).write(sb.toString());
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param type
     * @param propertiesfilePath
     * @return
     */
    private String getDataProvider(String type, String propertiesfilePath) {
        String dataProvider = null;
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("dataProvider." + type + ".type=")) {
                    dataProvider = line.replaceAll("dataProvider." + type + ".type=", "");
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        return dataProvider;
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void addScenarioInEnvPropertiesFile(String scenarioName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + "scenarios.properties";
        if (verbose) {
            logger.info("Add scenario named [{}] in scenario.properties.", scenarioName);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!(scenarioName + "=/steps/scenarios/").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    if ("###############   Scenario in production          ###############".equals(line)) {
                        sb.append("#################################################################");
                        sb.append(System.lineSeparator());
                        sb.append(scenarioName + "=/steps/scenarios/");
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updatePropertiesFile(propertiesfilePath, sb);
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void removeScenarioInEnvPropertiesFile(String scenarioName, boolean verbose) {
        String propertiesfilePath = mainPath + File.separator + RESOURCES + File.separator + "scenarios.properties";
        if (verbose) {
            logger.info("Remove scenario named [{}] in scenario.properties.", scenarioName);
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            String line = br.readLine();
            while (line != null) {
                if (!(scenarioName + "=/steps/scenarios/").equals(line)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
        updatePropertiesFile(propertiesfilePath, sb);
    }

    /**
     * @param scenarioName
     * @param description
     * @param applicationName
     * @param verbose
     */
    private void addScenarioFeature(String scenarioName, String description, String applicationName, boolean verbose) {
        String newFeaturePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "steps" + File.separator + "scenarios" + File.separator + scenarioName + ".feature";
        StringBuilder sb = new StringBuilder();
        sb.append("@" + scenarioName).append(System.lineSeparator());
        sb.append("Feature: " + scenarioName + " (" + description + ")").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("  Scenario Outline:  " + description + "").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    Given I check that user '<user>' is not empty.").append(System.lineSeparator());
        sb.append("    Given I check that password '<password>' is not empty.").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    Given '" + applicationName.toUpperCase() + "_HOME' is opened.").append(System.lineSeparator());
        sb.append("    Then The " + applicationName.toUpperCase() + " home page is displayed").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("    And I go back to '" + applicationName.toUpperCase() + "_HOME'").append(System.lineSeparator());
        sb.append("").append(System.lineSeparator());
        sb.append("  Examples:").append(System.lineSeparator());
        sb.append("    #DATA").append(System.lineSeparator());
        sb.append("    |id|user|password|").append(System.lineSeparator());
        sb.append("    #END").append(System.lineSeparator());
        try {
            File newFeature = new File(newFeaturePath);
            if (!newFeature.exists()) {
                Files.asCharSink(newFeature, Charsets.UTF_8).write(sb.toString());
                if (verbose) {
                    logger.info("File [{}] created with success.", newFeaturePath);
                }
            } else {
                if (verbose) {
                    logger.info("File [{}] already exist.", newFeaturePath);
                }
            }
        } catch (Exception e) {
            logger.error(TECHNICAL_IO_EXCEPTION, e.getMessage(), e);
        }
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void removeScenarioFeature(String scenarioName, boolean verbose) {
        String featurePath = "src" + File.separator + "test" + File.separator + RESOURCES + File.separator + "steps" + File.separator + "scenarios" + File.separator + scenarioName + ".feature";
        try {
            FileUtils.forceDelete(new File(featurePath));
            if (verbose) {
                logger.info("{} removed with success.", featurePath);
            }
        } catch (Exception e) {
            logger.debug("{} not revove because do not exist.", featurePath);
        }
    }

}
