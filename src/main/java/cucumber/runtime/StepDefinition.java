/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.runtime;

import java.util.List;

import gherkin.pickles.PickleStep;
import io.cucumber.stepexpression.Argument;

public interface StepDefinition {
    /**
     * Returns a list of arguments. Return null if the step definition
     * doesn't match at all. Return an empty List if it matches with 0 arguments
     * and bigger sizes if it matches several.
     * 
     * @param step
     * @return
     */
    List<Argument> matchedArguments(PickleStep step);

    /**
     * The source line where the step definition is defined.
     * Example: foo/bar/Zap.brainfuck:42
     *
     * @param detail
     *            true if extra detailed location information should be included.
     * @return
     */
    String getLocation(boolean detail);

    /**
     * How many declared parameters this step definition has. Returns null if unknown.
     * 
     * @return
     */
    Integer getParameterCount();

    /**
     * list of declared parameters this step definition has. Returns null if unknown.
     * 
     * @return
     */
    List<?> getParameters();

    /**
     * Invokes the step definition. The method should raise a Throwable
     * if the invocation fails, which will cause the step to fail.
     * 
     * @param args
     * @throws Throwable
     */
    void execute(Object[] args) throws Throwable;

    /**
     * Return true if this matches the location. This is used to filter
     * stack traces.
     * 
     * @param stackTraceElement
     * @return
     */
    boolean isDefinedAt(StackTraceElement stackTraceElement); // TODO: redundant with getLocation?

    /**
     * @return the pattern associated with this instance. Used for error reporting only.
     */
    String getPattern();

    /**
     * @return true if this instance is scoped to a single scenario, or false if it can be reused across scenarios.
     */
    boolean isScenarioScoped();

}
