/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.indus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.github.noraui.data.DataIndex;
import com.github.noraui.data.DataUtils;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.main.ScenarioInitiator;
import com.github.noraui.model.Model;
import com.github.noraui.model.ModelList;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

@Loggable
public class MavenRunCounter {

    static Logger log;

    private static final String STEP_KEYWORDS = "(Given|When|Then|And|But|Soit|Lorsqu|Quand|Alors|Et|Mais)";
    private static final String NEW_SCENARIO_OUTLINE = "(Scenario Outline:|Plan du Scénario:)";

    public static final String Z_MANAGER = "zManager";

    /**
     * Runs the counting process to figure out expected test runs results (Runs, Failures, Errors, Skipped).
     * 
     * @param versionControlSystemsBlacklist
     *            a list of control system files that won't be counted
     * @param blacklist
     *            a list of scenarios that won't be counted
     * @param manager
     *            a list of scenario that should be run at the very end
     * @param scenarioFolder
     *            root folder of Gherkin scenarios
     * @return
     *         a list of {@link Counter}
     * @throws TechnicalException
     *             is throws if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with message and with screenshot and with exception if functional error but no screenshot and no exception if technical error.
     */
    public List<Counter> count(List<String> versionControlSystemsBlacklist, List<String> blacklist, List<String> manager, File scenarioFolder) throws TechnicalException {
        final List<Counter> result = new ArrayList<>();
        final List<String> files = listFilesForFolder(versionControlSystemsBlacklist, scenarioFolder);

        final Pattern recommendedPattern = Pattern.compile("^[ ]{4}" + STEP_KEYWORDS);
        final Pattern requiredPattern = Pattern.compile("^[\\s]*" + STEP_KEYWORDS);
        final Pattern newScenario = Pattern.compile("^[\\s]*" + NEW_SCENARIO_OUTLINE);

        for (final String file : files) {
            final String scenarioName = file.substring(file.lastIndexOf(File.separator) + 1, file.lastIndexOf('.'));
            if (!blacklist.contains(scenarioName)) {
                int nbStep = 0;
                int nbScenario = 0;
                Counter counter = null;
                Matcher matcher;
                try (BufferedReader br = new BufferedReader(new FileReader(file));) {
                    String sCurrentLine;

                    while ((sCurrentLine = br.readLine()) != null) {
                        matcher = newScenario.matcher(sCurrentLine);
                        if (matcher.find()) {
                            if (counter != null) {
                                countAndAddToList(manager, result, scenarioName, nbStep, counter);
                            }
                            nbScenario++;
                            counter = new Counter(scenarioName, nbScenario);
                            nbStep = 0;

                        } else {
                            matcher = requiredPattern.matcher(sCurrentLine);
                            if (matcher.find()) {
                                nbStep++;
                                matcher = recommendedPattern.matcher(sCurrentLine);
                                if (!matcher.find()) {
                                    log.error("{} : {}", Messages.getMessage(Messages.SCENARIO_ERROR_MESSAGE_ILLEGAL_TAB_FORMAT), sCurrentLine);
                                }
                            }
                        }
                    }
                } catch (final IOException e) {
                    log.error("IOException error: ", e);
                }
                if (counter != null) {
                    countAndAddToList(manager, result, scenarioName, nbStep, counter);
                } else {
                    throw new TechnicalException(Messages.format(Messages.getMessage(Messages.SCENARIO_ERROR_MESSAGE_SCENARIO_OUTLINE_IS_MANDATORY, scenarioName)));
                }
            }
        }
        return result;
    }

    private void countAndAddToList(List<String> manager, final List<Counter> result, final String scenarioName, int nbStep, Counter counter) {
        countNbCasFailuresAndSkipped(scenarioName, counter, nbStep);
        result.add(counter);
        if (manager.contains(scenarioName)) {
            countNbCasFailuresAndSkipped(Z_MANAGER + scenarioName, new Counter(counter), nbStep);
            result.add(counter);
        }
    }

    private void countNbCasFailuresAndSkipped(String scenarioName, Counter counter, int nbStep) {
        final Counter nb = countNbCasFailuresAndSkipped(scenarioName, counter.getNbScenario(), nbStep);
        counter.setNbStep(nbStep);
        counter.setNbCas(nb.getNbCas());
        counter.setRun(nbStep * nb.getNbCas() + nb.getNbCas());
        counter.setFailures(nb.getFailures());
        counter.setSkipped(nb.getSkipped());
    }

    public void print(List<Counter> counters, String type) {
        int run = 0;
        int failures = 0;
        int skipped = 0;
        int scenarios = 0;
        Collections.sort(counters, new Counter("", 1));
        for (final MavenRunCounter.Counter counter : counters) {
            run += counter.getRun();
            failures += counter.getFailures();
            skipped += counter.getSkipped();
            scenarios += counter.getNbCas();
            log.info("Scenario: {} => step: {} and cases: {} -->  runs: {}, failures: {}, errors: 0 and skips: {}", counter.getScenarioName(), counter.getNbStep(), counter.getNbCas(),
                    counter.getRun(), counter.getFailures(), counter.getSkipped());
        }
        if (log.isInfoEnabled()) {
            log.info("{}", generateExpected1(type, failures, scenarios));
            log.info("{}", generateExpected2(type, run, failures, skipped, scenarios));
        }
    }

    public static List<String> listFilesForFolder(final List<String> versionControlSystemsBlacklist, final File folder) {
        final List<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!stringContainsItemFromList(fileEntry.getAbsolutePath(), versionControlSystemsBlacklist)) {
                if (fileEntry.isDirectory()) {
                    files.addAll(listFilesForFolder(versionControlSystemsBlacklist, fileEntry));
                } else {
                    if (!fileEntry.getAbsolutePath().contains(Z_MANAGER + File.separator + Z_MANAGER)) {
                        files.add(fileEntry.getAbsolutePath());
                    }
                }
            }
        }
        return files;
    }

    protected String generateExpected2(String type, int run, int failures, int skipped, int scenarios) {
        final StringBuilder expectedResults2 = new StringBuilder(100);
        int passed;
        expectedResults2.append("[").append(type).append("] > <EXPECTED_RESULTS_2>");
        expectedResults2.append(run - scenarios).append(" Step");
        if (scenarios > 0) {
            expectedResults2.append("s (");
        }
        if (failures > 0) {
            expectedResults2.append(failures).append(" failed, ");
            expectedResults2.append(skipped).append(" skipped, ");
        }
        passed = run - scenarios - failures - skipped;
        if (passed > 0) {
            expectedResults2.append(passed).append(" passed");
        }
        if (scenarios > 0) {
            expectedResults2.append(")");
        }
        expectedResults2.append("</EXPECTED_RESULTS_2>");
        return expectedResults2.toString();
    }

    protected String generateExpected1(String type, int failures, int scenarios) {
        final StringBuilder expectedResults1 = new StringBuilder(100);
        int passed;
        expectedResults1.append("[").append(type).append("] > <EXPECTED_RESULTS_1>");
        expectedResults1.append(scenarios).append(" Scenario");
        if (scenarios > 0) {
            expectedResults1.append("s (");
        }
        if (failures > 0) {
            expectedResults1.append(failures).append(" failed, ");
        }
        passed = scenarios - failures;
        if (passed > 0) {
            expectedResults1.append(passed).append(" passed");
        } else {
            if (failures > 0) {
                expectedResults1.deleteCharAt(expectedResults1.length() - 1);
                expectedResults1.deleteCharAt(expectedResults1.length() - 1);
            }
        }
        if (scenarios > 0) {
            expectedResults1.append(")");
        }
        expectedResults1.append("</EXPECTED_RESULTS_1>");
        return expectedResults1.toString();
    }

    /**
     * Class representing all expectation counting data for a single scenario in a Feature.
     * 
     * @author Nicolas HALLOUIN
     * @author Stéphane GRILLON
     */
    public class Counter implements Comparator<Counter> {
        private String scenarioName;
        private int nbStep;
        private int nbCas;
        private int run;
        private int failures;
        private int skipped;
        private int nbScenario;

        public Counter(String scenarioName, int nbScenario) {
            this.scenarioName = scenarioName + "#" + nbScenario;
            this.nbStep = 0;
            this.nbCas = 0;
            this.run = 0;
            this.failures = 0;
            this.skipped = 0;
            this.nbScenario = nbScenario;
        }

        public Counter(Counter counter) {
            this.scenarioName = counter.scenarioName;
            this.nbStep = counter.nbStep;
            this.nbCas = counter.nbCas;
            this.run = counter.run;
            this.failures = counter.failures;
            this.skipped = counter.skipped;
            this.nbScenario = counter.nbScenario;
        }

        @Override
        public int compare(Counter c1, Counter c2) {
            return c1.getScenarioName().compareTo(c2.getScenarioName());
        }

        public String getScenarioName() {
            return scenarioName;
        }

        public void setScenarioName(String scenarioName) {
            this.scenarioName = scenarioName;
        }

        public int getNbStep() {
            return nbStep;
        }

        public void setNbStep(int nbStep) {
            this.nbStep = nbStep;
        }

        public int getNbCas() {
            return nbCas;
        }

        public void setNbCas(int nbCas) {
            this.nbCas = nbCas;
        }

        public int getRun() {
            return run;
        }

        public void setRun(int run) {
            this.run = run;
        }

        public int getFailures() {
            return failures;
        }

        public void setFailures(int failures) {
            this.failures = failures;
        }

        public int getSkipped() {
            return skipped;
        }

        public void setSkipped(int skipped) {
            this.skipped = skipped;
        }

        public int getNbScenario() {
            return nbScenario;
        }

        public void setNbScenario(int nbScenario) {
            this.nbScenario = nbScenario;
        }

    }

    private static MavenRunCounter.Counter countNbCasFailuresAndSkipped(String scenarioName, int nbScenario, int nbStep) {
        final Counter result = new MavenRunCounter().new Counter("", nbScenario);
        final List<DataIndex> indexData = new ArrayList<>();
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            final Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model != null) {
                countWithModel(nbStep, result, indexData, model);
            } else {
                countWithoutModel(nbStep, result, indexData);
            }
        } catch (final Exception e) {
            log.error("error MavenRunCounter.countNbCasFailuresAndSkipped()", e);
        }
        return result;
    }

    private static void countWithoutModel(int nbStep, Counter result, List<DataIndex> indexData) throws TechnicalException {
        int failures = 0;
        int skipped = 0;
        int currentNbScenario = 1;
        for (int i = 1; i < Context.getDataInputProvider().getNbLines() + result.getNbScenario(); i++) {
            if (null == Context.getDataInputProvider().readLine(i, true)) {
                currentNbScenario++;
            } else {
                if (currentNbScenario == result.getNbScenario()) {
                    final List<Integer> index = new ArrayList<>();
                    index.add(i);
                    indexData.add(new DataIndex(i, index));
                    final String resultColumn = Context.getDataInputProvider().readValue(Context.getDataInputProvider().getResultColumnName(), i);
                    if (!"".equals(resultColumn)) {
                        failures += 1;
                        skipped += nbStep - (int) Double.parseDouble(resultColumn);
                    }
                }
            }

        }
        result.setNbCas(indexData.size());
        result.setFailures(failures);
        result.setSkipped(skipped);
    }

    private static void countWithModel(int nbStep, Counter result, List<DataIndex> indexData, Class<Model> model) throws TechnicalException {
        int failures = 0;
        int skipped = 0;
        final String[] headers = Context.getDataInputProvider().readLine(0, false);
        if (headers != null) {
            log.info("getModelConstructor model: {}", model);
            StringBuilder sb1 = new StringBuilder();
            for (String s : headers) {
                sb1.append(s).append(" ");
            }
            log.info("getModelConstructor headers: {}", sb1);
            final Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
            final Map<Integer, Map<String, ModelList>> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
            int dataIndex = 0;
            for (final Entry<String, ModelList> e2 : fusionedData.get(result.getNbScenario() - 1).entrySet()) {
                dataIndex++;
                indexData.add(new DataIndex(dataIndex, e2.getValue().getIds()));
                for (int i = 0; i < e2.getValue().getIds().size(); i++) {
                    final Integer id = e2.getValue().getIds().get(i);
                    final String resultColumn = Context.getDataInputProvider().readValue(Context.getDataInputProvider().getResultColumnName(), id);
                    if (!"".equals(resultColumn)) {
                        failures += 1;
                        skipped += nbStep - (int) Double.parseDouble(resultColumn);
                    }
                }
            }
        } else {
            log.error(Messages.getMessage(ScenarioInitiator.SCENARIO_INITIATOR_ERROR_EMPTY_FILE));
        }
        result.setNbCas(indexData.size());
        result.setFailures(failures);
        result.setSkipped(skipped);
    }

    private static boolean stringContainsItemFromList(String inputString, List<String> items) {
        for (final String item : items) {
            if (inputString.contains(item)) {
                return true;
            }
        }
        return false;
    }

}
