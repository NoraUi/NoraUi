/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.gherkin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.github.noraui.Constants;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;

@Loggable
public class GherkinFactory {

    static Logger log;

    private static final String DATA = "#DATA";
    private static final String DATA_END = "#END";
    private static final String SCENARIO_OUTLINE_SPLIT = "Scenario Outline:";
    private static final String SCENARIO_OUTLINE_SPLIT_FR = "Plan du Scénario:";
    private static final String SCENARIO_EXAMPLE_COLUMNS_SEPARATOR = "|";
    private static final String GHERKIN_LANGUAGE_REGEX = "#[\\s]*language[\\s]*:[\\s]*(\\S*)";

    /**
     * Private constructor
     */
    private GherkinFactory() {
    }

    /**
     * @param filename
     *            name of input Gherkin file.
     * @param examplesTable
     *            is a table of data without headers. Various examples can be present in a single file.
     *            In this case, the key of each entry if the Hashtable matches the position of the examples table in the Gherkin file.
     */
    public static void injectDataInGherkinExamples(String filename, Map<Integer, List<String[]>> examplesTable) {
        try {
            if (!examplesTable.isEmpty()) {
                final Path filePath = getFeaturePath(filename);
                final String fileContent = new String(Files.readAllBytes(filePath), Constants.DEFAULT_ENDODING);
                String lang = getFeatureLanguage(fileContent);
                log.info(lang);
                StringBuilder examplesString;
                final String[] scenarioOutlines = "fr".equals(lang) ? fileContent.split(SCENARIO_OUTLINE_SPLIT_FR) : fileContent.split(SCENARIO_OUTLINE_SPLIT);
                for (final Entry<Integer, List<String[]>> examples : examplesTable.entrySet()) {
                    examplesString = new StringBuilder();
                    examplesString.append("    ");
                    for (int j = 0; j < examples.getValue().size(); j++) {
                        examplesString.append(SCENARIO_EXAMPLE_COLUMNS_SEPARATOR);
                        examplesString.append(j + 1);
                        for (final String col : examples.getValue().get(j)) {
                            examplesString.append(SCENARIO_EXAMPLE_COLUMNS_SEPARATOR);
                            examplesString.append(col);
                        }
                        examplesString.append(SCENARIO_EXAMPLE_COLUMNS_SEPARATOR + "\n    ");
                    }

                    scenarioOutlines[examples.getKey() + 1] = scenarioOutlines[examples.getKey() + 1].replaceAll("(" + DATA + "\r?\n.*\r?\n)[\\s\\S]*(" + DATA_END + ")",
                            "$1" + examplesString.toString() + "$2");
                }

                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString()), Constants.DEFAULT_ENDODING));) {
                    int i = 0;
                    bw.write(scenarioOutlines[i]);

                    while (++i < scenarioOutlines.length) {
                        if ("fr".equals(lang)) {
                            bw.write(SCENARIO_OUTLINE_SPLIT_FR + scenarioOutlines[i]);
                        } else {
                            bw.write(SCENARIO_OUTLINE_SPLIT + scenarioOutlines[i]);
                        }
                    }
                }
            }
        } catch (final IOException e) {
            log.error("error GherkinFactory.injectDataInGherkinExamples()", e);
        }
    }

    public static int getNumberOfGherkinExamples(String filename) {
        return getExamples(filename).length;
    }

    public static String getFeatureLanguage(String featureContent) {
        final Pattern pattern = Pattern.compile(GHERKIN_LANGUAGE_REGEX, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(featureContent);

        if (matcher.find() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        return "";

    }

    public static String[] getExamples(String filename) {
        try {
            final Path filePath = getFeaturePath(filename);
            final String fileContent = new String(Files.readAllBytes(filePath), Constants.DEFAULT_ENDODING);
            final Pattern pattern = Pattern.compile(DATA + "([\\s\\S]*)" + DATA_END);
            final Matcher matcher = pattern.matcher(fileContent);
            String lines = "";
            if (matcher.find() && matcher.groupCount() == 1) {
                lines = matcher.group(0);
            }
            final String[] examples = lines.split("\\n");
            // Return lines - #DATA - #END
            return examples.length > 2 ? Arrays.copyOfRange(examples, 1, examples.length - 1) : new String[] {};
        } catch (final IOException e) {
            log.error("error GherkinFactory.getExamples()", e);
        }
        return new String[] {};
    }

    private static Path getFeaturePath(String filename) {
        final int indexOfUnderscore = filename.lastIndexOf('_');
        final String path = indexOfUnderscore != -1
                ? Context.getResourcesPath() + Context.getScenarioProperty(filename.substring(0, indexOfUnderscore)) + filename.substring(0, indexOfUnderscore) + ".feature"
                : Context.getResourcesPath() + Context.getScenarioProperty(filename) + filename + ".feature";
        return Paths.get(path);
    }
}
