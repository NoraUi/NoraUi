/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package io.cucumber.core.runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.core.api.Scenario;
import io.cucumber.core.backend.ParameterInfo;
import io.cucumber.core.backend.StepDefinition;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.core.feature.CucumberStep;
import io.cucumber.core.stepexpression.Argument;
import io.cucumber.cucumberexpressions.CucumberExpressionException;
import io.cucumber.datatable.CucumberDataTableException;
import io.cucumber.datatable.UndefinedDataTableTypeException;

public class PickleStepDefinitionMatch extends Match implements StepDefinitionMatch {
    
    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PickleStepDefinitionMatch.class.getName());
    
    private final StepDefinition stepDefinition;
    private final transient String uri;
    // The official JSON gherkin format doesn't have a step attribute, so we're marking this as transient
    // to prevent it from ending up in the JSON.
    private final transient CucumberStep step;

    public PickleStepDefinitionMatch(List<Argument> arguments, StepDefinition stepDefinition, String uri, CucumberStep step) {
        super(arguments, stepDefinition.getLocation(false));
        this.stepDefinition = stepDefinition;
        this.uri = uri;
        this.step = step;
    }

    @Override
    public void runStep(Scenario scenario) throws Throwable {
        LOGGER.info("runStep {}", step.getText());
        
        List<Argument> arguments = getArguments();
        int argumentCount = arguments.size();
        
        List<ParameterInfo> parameterInfos = stepDefinition.parameterInfos();
        LOGGER.debug("parameterInfos:{} argumentCount:{}", step.getText(), parameterInfos, argumentCount);
        for (Argument ar : arguments) {
            LOGGER.debug("Argument: {}", ar);
        }
        
        if (parameterInfos != null && (argumentCount > parameterInfos.size() || argumentCount + 1 < parameterInfos.size())) {
            LOGGER.error("arityMismatch: {}", parameterInfos.size());
            throw arityMismatch(parameterInfos.size());
        }
        
        List<Object> result = new ArrayList<>();
        try {
            for (Argument argument : arguments) {
                LOGGER.debug("add argument {} to result", argument.getValue());
                result.add(argument.getValue());
            }
            // add List<GherkinStepCondition> or parameters Map<String, String>
            if (parameterInfos != null && argumentCount + 1 == parameterInfos.size()) {
                //List<?> parameters = stepDefinition.getParameters();
                Object obj;
                if (parameterInfos.get(parameterInfos.size()-1).getType().toString().startsWith("java.util.List<")) {
                    obj = new ArrayList<>();
                } else if (parameterInfos.get(parameterInfos.size()-1).getType().toString().startsWith("java.util.Map<")) {
                    obj = new HashMap<>();
                } else {
                    LOGGER.error("arityMismatch in add List<GherkinStepCondition> or parameters Map<String, String>: {}", parameterInfos.size());
                    throw arityMismatch(parameterInfos.size());
                }
                LOGGER.debug("add argument {} to result in add List<GherkinStepCondition> or parameters Map<String, String>", obj);
                result.add(obj);
            }
        } catch (UndefinedDataTableTypeException e) {
            LOGGER.error("UndefinedDataTableTypeException when add", e);
            throw registerTypeInConfiguration(e);
        } catch (CucumberExpressionException | CucumberDataTableException e) {
            LOGGER.error("CucumberExpressionException or CucumberDataTableException when add", e);
            throw couldNotConvertArguments(e);
        }

        try {
            LOGGER.debug("stepDefinition.execute {}", result.size());
            stepDefinition.execute(result.toArray(new Object[0]));
        } catch (CucumberException e) {
            LOGGER.error("CucumberException when stepDefinition.execute: {}", e);
            throw e;
        } catch (Throwable t) {
            LOGGER.error("Throwable when stepDefinition.execute: {}", t);
            throw removeFrameworkFramesAndAppendStepLocation(t, getStepLocation());
        }
    }

    private CucumberException registerTypeInConfiguration(Exception e) {
        return new CucumberException(String.format("" +
                "Could not convert arguments for step [%s] defined at '%s'.\n" +
                "It appears you did not register a data table type. The details are in the stacktrace below.", //TODO: Add doc URL
            stepDefinition.getPattern(),
            stepDefinition.getLocation(true)
        ), e);
    }


    private CucumberException couldNotConvertArguments(Exception e) {
        return new CucumberException(String.format(
            "Could not convert arguments for step [%s] defined at '%s'.\n" +
                "The details are in the stacktrace below.",
            stepDefinition.getPattern(),
            stepDefinition.getLocation(true)
        ), e);
    }

    @Override
    public void dryRunStep(Scenario scenario) throws Throwable {
        // Do nothing
    }

    private CucumberException arityMismatch(int parameterCount) {
        List<String> arguments = createArgumentsForErrorMessage();
        return new CucumberException(String.format(
            "Step [%s] is defined with %s parameters at '%s'.\n" +
                "However, the gherkin step has %s arguments%sStep text: %s",
            stepDefinition.getPattern(),
            parameterCount,
            stepDefinition.getLocation(true),
            arguments.size(),
            formatArguments(arguments),
            step.getText()
        ));
    }

    private String formatArguments(List<String> arguments) {
        if (arguments.isEmpty()) {
            return ".\n";
        }

        StringBuilder formatted = new StringBuilder(":\n");
        for (String argument : arguments) {
            formatted.append(" * ").append(argument).append("\n");
        }
        return formatted.toString();
    }

    private List<String> createArgumentsForErrorMessage() {
        List<String> arguments = new ArrayList<>(getArguments().size());
        for (Argument argument : getArguments()) {
            arguments.add(argument.toString());
        }
        return arguments;
    }

    Throwable removeFrameworkFramesAndAppendStepLocation(Throwable error, StackTraceElement stepLocation) {
        StackTraceElement[] stackTraceElements = error.getStackTrace();
        if (stackTraceElements.length == 0 || stepLocation == null) {
            return error;
        }

        int newStackTraceLength;
        for (newStackTraceLength = 1; newStackTraceLength < stackTraceElements.length; ++newStackTraceLength) {
            if (stepDefinition.isDefinedAt(stackTraceElements[newStackTraceLength - 1])) {
                break;
            }
        }
        StackTraceElement[] newStackTrace = new StackTraceElement[newStackTraceLength + 1];
        System.arraycopy(stackTraceElements, 0, newStackTrace, 0, newStackTraceLength);
        newStackTrace[newStackTraceLength] = stepLocation;
        error.setStackTrace(newStackTrace);
        return error;
    }

    public String getPattern() {
        return stepDefinition.getPattern();
    }

    StackTraceElement getStepLocation() {
        return new StackTraceElement("✽", step.getText(), uri, step.getStepLine());
    }

    public Match getMatch() {
        return this;
    }

    StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    @Override
    public String getCodeLocation() {
        return stepDefinition.getLocation(false);
    }

}

