/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.main;

import static com.github.noraui.utils.Constants.USER_DIR;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.data.DataUtils;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinFactory;
import com.github.noraui.model.Model;
import com.github.noraui.model.ModelList;
import com.github.noraui.utils.Context;
import com.github.noraui.utils.Messages;

public class ScenarioInitiator {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ScenarioInitiator.class);

    public static final String SCENARIO_INITIATOR_ERROR_EMPTY_FILE = "SCENARIO_INITIATOR_ERROR_EMPTY_FILE";
    private static final String SCENARIO_INITIATOR_ERROR_UNABLE_TO_GET_TAGS = "SCENARIO_INITIATOR_ERROR_UNABLE_TO_GET_TAGS";
    private static final String SCENARIO_INITIATOR_USAGE = "SCENARIO_INITIATOR_USAGE";
    private static final String SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL = "SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL";
    private static final String SCENARIO_INITIATOR_INJECT_WITH_MODEL = "SCENARIO_INITIATOR_INJECT_WITH_MODEL";
    private static final String SCENARIO_INITIATOR_ERROR_ON_INJECTING_MODEL = "SCENARIO_INITIATOR_ERROR_ON_INJECTING_MODEL";

    public void start(String[] args) {
        logger.info("Working Directory is '" + System.getProperty(USER_DIR) + "'");
        logger.info("ScenarioInitiator > start()");
        if (args != null && args.length == 1 && !"@TOSPECIFY".equals(args[0])) {
            logger.info("# {}", args[0]);
            final String scenarioName = args[0];
            processInjection(scenarioName);
        } else {
            logger.warn(Messages.getMessage(SCENARIO_INITIATOR_USAGE));
            String cucumberOptions = System.getProperty("cucumber.options");
            if (cucumberOptions != null && cucumberOptions.contains("--tags")) {
                Matcher matcher = Pattern.compile(".*--tags '(.*)'.*").matcher(cucumberOptions);
                if (matcher.find() && matcher.groupCount() > 0) {
                    String tags = matcher.group(1).replace("not ", "").replace(")", "").replace("(", "").replace(" and ", " ").replace(" or ", " ").replace("@", "");
                    for (final String s : tags.split(" ")) {
                        if (!s.startsWith("~")) {
                            processInjection(s);
                        }
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
                logger.info(Messages.getMessage(SCENARIO_INITIATOR_INJECT_WITHOUT_MODEL), scenarioName);
                injectWithoutModel(scenarioName);
            } else {
                logger.info(Messages.getMessage(SCENARIO_INITIATOR_INJECT_WITH_MODEL), scenarioName, model.getSimpleName());
                injectWithModel(scenarioName, model);
            }
        } catch (final Exception e) {
            logger.error("error ScenarioInitiator.processInjection()", e);
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
