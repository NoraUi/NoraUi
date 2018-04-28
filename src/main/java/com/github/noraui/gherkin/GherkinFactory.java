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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.utils.Constants;
import com.github.noraui.utils.Context;

public class GherkinFactory {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(GherkinFactory.class);

    private static final String DATA = "#DATA";
    private static final String DATA_END = "#END";

    /**
     * Private constructor
     */
    private GherkinFactory() {
    }

    /**
     * @param filename
     *            name of input Gherkin file.
     * @param lines
     *            is a table of data (line by line and without headers).
     */
    public static void injectDataInGherkinExamples(String filename, List<String[]> lines) {
        try {
            if (!lines.isEmpty()) {
                Path filePath = getFeaturePath(filename);
                String fileContent = new String(Files.readAllBytes(filePath), Charset.forName(Constants.DEFAULT_ENDODING));
                StringBuilder examples = new StringBuilder();
                examples.append("    ");
                for (int j = 0; j < lines.size(); j++) {
                    examples.append("|");
                    examples.append(j + 1);
                    for (String col : lines.get(j)) {
                        examples.append("|");
                        examples.append(col);
                    }
                    examples.append("|\n    ");
                }

                // fileContent = fileContent.replaceAll("(" + DATA + "\r?\n.*\r?\n)[\\s\\S]*(" + DATA_END + ")", "$1" + examples.toString() + "$2");
                fileContent = fileContent.replaceAll("(" + DATA + "\r?\n.*\r?\n)[\\s\\w]*(" + DATA_END + ")", "$1" + examples.toString() + "$2");

                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toString()), Charset.forName(Constants.DEFAULT_ENDODING)));) {
                    bw.write(fileContent);
                }
            }
        } catch (IOException e) {
            logger.error("error GherkinFactory.injectDataInGherkinExamples()", e);
        }
    }

    public static int getNumberOfGherkinExamples(String filename) {
        return getExamples(filename).length;
    }

    public static String[] getExamples(String filename) {
        try {
            Path filePath = getFeaturePath(filename);
            String fileContent = new String(Files.readAllBytes(filePath), Charset.forName(Constants.DEFAULT_ENDODING));
            Pattern pattern = Pattern.compile(DATA + "([\\s\\S]*)" + DATA_END);
            Matcher matcher = pattern.matcher(fileContent);
            String lines = "";
            if (matcher.find() && matcher.groupCount() == 1) {
                lines = matcher.group(0);
            }
            String[] examples = lines.split("\\n");
            // Return lines - #DATA - #END
            return examples.length > 2 ? Arrays.copyOfRange(examples, 1, examples.length - 1) : new String[] {};

        } catch (IOException e) {
            logger.error("error GherkinFactory.getExamples()", e);
        }
        return new String[] {};
    }

    private static Path getFeaturePath(String filename) {
        int indexOfUnderscore = filename.lastIndexOf('_');
        String path = indexOfUnderscore != -1
                ? Context.getResourcesPath() + Context.getScenarioProperty(filename.substring(0, indexOfUnderscore)) + filename.substring(0, indexOfUnderscore) + ".feature"
                : Context.getResourcesPath() + Context.getScenarioProperty(filename) + filename + ".feature";
        return Paths.get(path);
    }
}
