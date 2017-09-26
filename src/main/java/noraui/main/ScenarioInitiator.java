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
import noraui.utils.Messages;

public class ScenarioInitiator {

    public static final String SCENARIO_INITIATOR_ERROR_EMPTY_FILE = "SCENARIO_INITIATOR_ERROR_EMPTY_FILE";

    private static final Logger logger = Logger.getLogger(ScenarioInitiator.class);
    private static final String SCENARIO_INITIATOR_ERROR_UNABLE_TO_GET_TAGS = "SCENARIO_INITIATOR_ERROR_UNABLE_TO_GET_TAGS";
    private static final String SCENARIO_INITIATOR_USAGE = "SCENARIO_INITIATOR_USAGE";
    private static final String SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL = "SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL";
    private static final String SCENARIO_INITIATOR_INJECT_WITH_MODEL = "SCENARIO_INITIATOR_INJECT_WITH_MODEL";
    private static final String SCENARIO_INITIATOR_ERROR_ON_INJECTING_MODEL = "SCENARIO_INITIATOR_ERROR_ON_INJECTING_MODEL";

    public void start(String[] args) {
        logger.info("Working Directory is '" + System.getProperty("user.dir") + "'");
        logger.info("ScenarioInitiator > start()");
        if (args != null && args.length == 1 && !"@TOSPECIFY".equals(args[0])) {
            logger.info("# " + args[0]);
            final String scenarioName = args[0];
            processInjection(scenarioName);
        } else {
            logger.warn(Messages.getMessage(SCENARIO_INITIATOR_USAGE));
            String tags = System.getProperty("cucumber.options");
            if (tags != null && tags.contains("--tags")) {
                tags = tags.replace("'", "").replace("--tags @", "").replace("@", "");
                for (final String s : tags.split(" or ")) {
                    if (!s.startsWith("~")) {
                        processInjection(s);
                    }
                }
            } else {
                logger.error(Messages.getMessage(SCENARIO_INITIATOR_ERROR_UNABLE_TO_GET_TAGS));
            }
        }

    }

    private static void processInjection(String scenarioName) {
        try {
            Context.getDataInputProvider().prepare(scenarioName);
            final Class<Model> model = Context.getDataInputProvider().getModel(Context.getModelPackages());
            if (model == null) {
                logger.info(String.format(Messages.getMessage(SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL), scenarioName));
                injectWithoutModel(scenarioName);
            } else {
                logger.info(String.format(Messages.getMessage(SCENARIO_INITIATOR_INJECT_WITH_MODEL), scenarioName, model.getSimpleName()));
                injectWithModel(scenarioName, model);
            }
        } catch (final Exception e) {
            logger.error(e);
        }
    }

    private static void injectWithoutModel(String scenarioName) throws TechnicalException {
        final List<String[]> examples = new ArrayList<>();
        String[] example;
        for (int i = 1; (example = Context.getDataInputProvider().readLine(i, false)) != null; i++) {
            examples.add(example);
        }
        GherkinFactory.injectDataInGherkinExamples(scenarioName, examples);
    }

    private static void injectWithModel(String scenarioName, Class<Model> model) throws TechnicalException {
        try {
            final String[] headers = Context.getDataInputProvider().readLine(0, false);
            if (headers != null) {
                final List<String[]> examples = new ArrayList<>();
                final Constructor<Model> modelConstructor = DataUtils.getModelConstructor(model, headers);
                final Map<String, ModelList> fusionedData = DataUtils.fusionProcessor(model, modelConstructor);
                for (final Entry<String, ModelList> e : fusionedData.entrySet()) {
                    examples.add(new String[] { e.getKey(), e.getValue().serialize() });
                }
                GherkinFactory.injectDataInGherkinExamples(scenarioName, examples);
            } else {
                logger.error(Messages.getMessage(SCENARIO_INITIATOR_ERROR_EMPTY_FILE));
            }
        } catch (final Exception te) {
            throw new TechnicalException(Messages.getMessage(SCENARIO_INITIATOR_ERROR_ON_INJECTING_MODEL) + te.getMessage(), te);
        }
    }

}
