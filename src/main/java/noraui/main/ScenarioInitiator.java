package noraui.main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import noraui.data.DataUtils;
import noraui.exception.TechnicalException;
import noraui.gherkin.GherkinFactory;
import noraui.model.Model;
import noraui.model.ModelList;
import noraui.utils.Context;

public class ScenarioInitiator {

    private static final Logger logger = Logger.getLogger(ScenarioInitiator.class);

    public void start(String[] args) {

        Context.initializeScenarioProperties(ScenarioInitiator.class.getClassLoader());
        logger.info("Working Directory = " + System.getProperty("user.dir"));
        logger.info("ScenarioInitiator.start:");
        if (args != null && args.length == 1 && !"@TOSPECIFY".equals(args[0])) {
            for (String a : args) {
                logger.info("# " + a);
            }
            String scenarioName = args[0];
            processInjection(scenarioName);
        } else {
            logger.warn("Usage: ScenarioInitiator main must be launched with 1 parameters ==> \"ScenarioName\"\nUsing tags instead");
            String tags = System.getProperty("cucumber.options");
            if (tags != null && tags.contains("--tags")) {
                tags = tags.replace("--tags @", "").replace("@", "");
                for (String s : tags.split(",")) {
                    processInjection(s);
                }
            } else {
                logger.error("Unable to get tags to process injection");
            }
        }

    }

    private static void processInjection(String scenarioName) {
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model == null) {
                logger.info("ScenarioInitiator > processInjection(" + scenarioName + ") without using model");
                injectWithoutModel(scenarioName);
            } else {
                logger.info("ScenarioInitiator > processInjection(" + scenarioName + ") using '" + model.getSimpleName() + "' model");
                injectWithModel(scenarioName, model);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void injectWithoutModel(String scenarioName) throws TechnicalException {
        List<String[]> examples = new ArrayList<>();
        String[] example;
        for (int i = 1; (example = Context.getDataInputProvider().readLine(i, false)) != null; i++) {
            examples.add(example);
        }
        GherkinFactory.injectDataInGherkinExamples(scenarioName, examples);
    }

    private static void injectWithModel(String scenarioName, Class<Model> model) throws TechnicalException {
        try {
            String[] headers = Context.getDataInputProvider().readLine(0, false);

            if (headers != null) {
                List<String[]> examples = new ArrayList<>();
                Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
                Map<String, ModelList> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
                for (Entry<String, ModelList> e : fusionedData.entrySet()) {
                    examples.add(new String[] { e.getKey(), e.getValue().serialize() });
                }
                GherkinFactory.injectDataInGherkinExamples(scenarioName, examples);
            } else {
                logger.error("Data file is empty. No injection has been done !");
            }
        } catch (Exception te) {
            logger.error("Technical problem during injectWithModel: " + te.getMessage());
            throw new TechnicalException("Technical problem during injectWithModel: " + te.getMessage(), te);
        }
    }

}
