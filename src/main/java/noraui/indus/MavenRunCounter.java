package noraui.indus;

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

import org.apache.log4j.Logger;

import noraui.data.DataIndex;
import noraui.data.DataProvider;
import noraui.data.DataUtils;
import noraui.exception.TechnicalException;
import noraui.model.Model;
import noraui.model.ModelList;
import noraui.utils.Context;
import noraui.utils.Messages;

public class MavenRunCounter {

    private static Logger logger = Logger.getLogger(MavenRunCounter.class.getName());

    public static final String Z_MANAGER = "zManager";

    public List<Counter> count(List<String> versionControlSystemsBlacklist, List<String> blacklist, List<String> manager, File scenarioFolder) {
        List<Counter> result = new ArrayList<>();
        List<String> files = listFilesForFolder(versionControlSystemsBlacklist, scenarioFolder);
        for (String file : files) {
            String scenarioName = file.substring(file.lastIndexOf(File.separator) + 1, file.lastIndexOf('.'));
            if (!blacklist.contains(scenarioName)) {
                Counter counter = new Counter(scenarioName);
                int nbStep = 0;
                try (BufferedReader br = new BufferedReader(new FileReader(file));) {
                    String sCurrentLine;
                    while ((sCurrentLine = br.readLine()) != null) {
                        if (sCurrentLine.startsWith("    Given") || sCurrentLine.startsWith("    Then") || sCurrentLine.startsWith("    When") || sCurrentLine.startsWith("    And")
                                || sCurrentLine.startsWith("    But") || sCurrentLine.startsWith("    Alors") || sCurrentLine.startsWith("    Et") || sCurrentLine.startsWith("    Lorsqu")
                                || sCurrentLine.startsWith("    Mais") || sCurrentLine.startsWith("    Quand") || sCurrentLine.startsWith("    Soit")) {
                            nbStep++;
                        } else {
                            sCurrentLine = sCurrentLine.trim();
                            if (sCurrentLine.startsWith("Given") || sCurrentLine.startsWith("Then") || sCurrentLine.startsWith("When") || sCurrentLine.startsWith("And")
                                    || sCurrentLine.startsWith("But") || sCurrentLine.startsWith("Alors") || sCurrentLine.startsWith("Et") || sCurrentLine.startsWith("Lorsqu")
                                    || sCurrentLine.startsWith("Mais") || sCurrentLine.startsWith("Quand") || sCurrentLine.startsWith("Soit")) {
                                logger.error(Messages.SCENARIO_ERROR_MESSAGE_ILLEGAL_TAB_FORMAT + " : " + sCurrentLine);
                            }
                        }
                    }
                } catch (IOException e) {
                    logger.error("IOException error: " + e);
                }
                countNbCasFailuresAndSkipped(scenarioName, counter, nbStep);
                result.add(counter);
                if (manager.contains(scenarioName)) {
                    counter = countNbCasFailuresAndSkipped4Manager(scenarioName, nbStep);
                    result.add(counter);
                }
            }
        }
        return result;
    }

    private Counter countNbCasFailuresAndSkipped4Manager(String scenarioName, int nbStep) {
        Counter counter;
        Counter nb;
        counter = new Counter(Z_MANAGER + scenarioName);
        nb = countNbCasFailuresAndSkipped(Z_MANAGER + scenarioName, nbStep);
        counter.setNbStep(nbStep);
        counter.setNbcas(nb.getNbCas());
        counter.setRun(nbStep * nb.getNbCas() + nb.getNbCas());
        counter.setFailures(nb.getFailures());
        counter.setSkipped(nb.getSkipped());
        return counter;
    }

    private void countNbCasFailuresAndSkipped(String scenarioName, Counter counter, int nbStep) {
        Counter nb = countNbCasFailuresAndSkipped(scenarioName, nbStep);
        counter.setNbStep(nbStep);
        counter.setNbcas(nb.getNbCas());
        counter.setRun(nbStep * nb.getNbCas() + nb.getNbCas());
        counter.setFailures(nb.getFailures());
        counter.setSkipped(nb.getSkipped());
    }

    public void print(List<Counter> counters, String type) {
        int run = 0;
        int failures = 0;
        int skipped = 0;
        Collections.sort(counters, new Counter(""));
        for (MavenRunCounter.Counter counter : counters) {
            run += counter.getRun();
            failures += counter.getFailures();
            skipped += counter.getSkipped();
            logger.info("Scenario: " + counter.getScenarioName() + " => NbStep: " + counter.getNbStep() + " and Nb Cas: " + counter.getNbCas() + " -->  runs: " + counter.getRun() + ", failures: "
                    + counter.getFailures() + ", errors: 0 and skips: " + counter.getSkipped());
        }
        logger.info("[" + type + "] > <EXPECTED_RESULTS>Tests run: " + run + ", Failures: " + failures + ", Errors: 0, Skipped: " + skipped + "</EXPECTED_RESULTS>");
    }

    public static List<String> listFilesForFolder(final List<String> versionControlSystemsBlacklist, final File folder) {
        List<String> files = new ArrayList<>();
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

    public class Counter implements Comparator<Counter> {
        String scenarioName;
        int nbStep;
        int nbCas;
        int run;
        int failures;
        int skipped;

        public Counter(String scenarioName) {
            this.scenarioName = scenarioName;
            this.nbStep = 0;
            this.nbCas = 0;
            this.run = 0;
            this.failures = 0;
            this.skipped = 0;
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

        public void setNbcas(int nbCas) {
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

    }

    private static MavenRunCounter.Counter countNbCasFailuresAndSkipped(String scenarioName, int nbStep) {
        Counter result = new MavenRunCounter().new Counter("");
        List<DataIndex> indexData = new ArrayList<>();
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model != null) {
                countWithModel(nbStep, result, indexData, model);
            } else {
                countWithoutModel(nbStep, result, indexData);
            }
        } catch (Exception te) {
            logger.error(te);
        }
        return result;
    }

    private static void countWithoutModel(int nbStep, Counter result, List<DataIndex> indexData) throws TechnicalException {
        int failures = 0;
        int skipped = 0;
        for (int i = 1; i < Context.getDataInputProvider().getNbLines(); i++) {
            List<Integer> index = new ArrayList<>();
            index.add(i);
            indexData.add(new DataIndex(i, index));
            String resultColumn = Context.getDataInputProvider().readValue(DataProvider.NAME_OF_RESULT_COLUMN, i);
            if (!"".equals(resultColumn)) {
                failures += 2;
                skipped += nbStep - (int) Double.parseDouble(resultColumn);
            }
        }
        result.setNbcas(indexData.size());
        result.setFailures(failures);
        result.setSkipped(skipped);
    }

    private static void countWithModel(int nbStep, Counter result, List<DataIndex> indexData, Class<Model> model) throws TechnicalException {
        int failures = 0;
        int skipped = 0;
        String[] headers = Context.getDataInputProvider().readLine(0, false);
        if (headers != null) {
            Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
            Map<String, ModelList> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
            int dataIndex = 0;
            for (Entry<String, ModelList> e : fusionedData.entrySet()) {
                dataIndex++;
                indexData.add(new DataIndex(dataIndex, e.getValue().getIds()));
                for (int i = 0; i < e.getValue().getIds().size(); i++) {
                    Integer wid = e.getValue().getIds().get(i);
                    String resultColumn = Context.getDataInputProvider().readValue(DataProvider.NAME_OF_RESULT_COLUMN, wid);
                    if (!"".equals(resultColumn)) {
                        failures += 2;
                        skipped += nbStep - (int) Double.parseDouble(resultColumn);
                    }
                }
            }
        } else {
            logger.error("Data file is empty. No injection has been done !");
        }
        result.setNbcas(indexData.size());
        result.setFailures(failures);
        result.setSkipped(skipped);
    }

    private static boolean stringContainsItemFromList(String inputString, List<String> items) {
        for (String item : items) {
            if (inputString.contains(item)) {
                return true;
            }
        }
        return false;
    }

}
