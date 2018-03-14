/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Scenario {

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
        System.out.println("Add a new scenario named [" + scenarioName + "] on [" + applicationName + "] application with this description: " + description);
        addScenarioInData(scenarioName, noraRobotName, verbose);
        addScenarioInEnvPropertiesFile(scenarioName, verbose);
        addScenarioFeature(scenarioName, description, applicationName, verbose);
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void addScenarioInData(String scenarioName, String noraRobotName, boolean verbose) {
        String propertiesfilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + noraRobotName + ".properties";

        String dataProviderIn = getDataProvider("in", propertiesfilePath);
        System.out.println("dataProvider.in.type is [" + dataProviderIn + "]");
        addScenarioInData("in", scenarioName, dataProviderIn);

        String dataProviderOut = getDataProvider("out", propertiesfilePath);
        System.out.println("dataProvider.out.type is [" + dataProviderOut + "]");
        addScenarioInData("out", scenarioName, dataProviderOut);
    }

    /**
     * @param type
     * @param scenarioName
     * @param dataProviderIn
     */
    private void addScenarioInData(String type, String scenarioName, String dataProviderIn) {
        if ("CSV".equals(dataProviderIn)) {
            String csvPath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".csv";
            File newCsvfile = new File(csvPath);
            if (!newCsvfile.exists()) {
                addCsvFile(newCsvfile);
            }
        } else if ("DB".equals(dataProviderIn)) {
            String excelPath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".sql";
            File sqlFile = new File(excelPath);
            if (!sqlFile.exists()) {
                addSqlFile(scenarioName, sqlFile);
            }
        } else if ("EXCEL".equals(dataProviderIn)) {
            String excelPath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "data" + File.separator + type + File.separator + scenarioName + ".xlsx";
            File newExcelFile = new File(excelPath);
            if (!newExcelFile.exists()) {
                addXlsxFile(scenarioName, excelPath);
            }
        }
    }

    /**
     * @param scenarioName
     * @param excelPath
     */
    private void addXlsxFile(String scenarioName, String excelPath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
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
        try (FileOutputStream outputStream = new FileOutputStream(excelPath)) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            System.err.println("IOException " + e);
            System.exit(1);
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
            System.err.println("IOException " + e);
            System.exit(1);
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
            System.err.println("IOException " + e);
            System.exit(1);
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
            System.err.println("IOException " + e);
            System.exit(1);
        }
        return dataProvider;
    }

    /**
     * @param scenarioName
     * @param verbose
     */
    private void addScenarioInEnvPropertiesFile(String scenarioName, boolean verbose) {
        String propertiesfilePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "scenarios.properties";
        if (verbose) {
            System.out.println("Add scenario named [" + scenarioName + "] in scenario.properties.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(propertiesfilePath))) {
            StringBuilder sb = new StringBuilder();
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
            FileWriter fw = new FileWriter(propertiesfilePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(sb.toString().substring(0, sb.toString().length() - System.lineSeparator().length()));
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

    /**
     * @param scenarioName
     * @param description
     * @param applicationName
     * @param verbose
     */
    private void addScenarioFeature(String scenarioName, String description, String applicationName, boolean verbose) {
        String newFeaturePath = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "steps" + File.separator + "scenarios" + File.separator + scenarioName + ".feature";
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
            }
        } catch (Exception e) {
            System.err.println("IOException " + e);
            System.exit(1);
        }
    }

}
