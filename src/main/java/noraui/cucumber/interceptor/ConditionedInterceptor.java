package noraui.cucumber.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import noraui.cucumber.annotation.Conditioned;
import noraui.gherkin.GherkinCondition;
import noraui.utils.Context;

public class ConditionedInterceptor implements MethodInterceptor {

    private static Logger logger = Logger.getLogger(ConditionedInterceptor.class.getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //
        Method m = invocation.getMethod();

        if (m.isAnnotationPresent(Conditioned.class)) {
            Object[] arg = invocation.getArguments();
            if (arg.length > 0 && arg[arg.length - 1] instanceof List && !((List) arg[arg.length - 1]).isEmpty() && ((List) arg[arg.length - 1]).get(0) instanceof GherkinCondition) {
                List<GherkinCondition> conditions = (List) arg[arg.length - 1];
                displayMessageAtTheBeginningOfMethod(m.getName(), conditions);
                if (!checkConditions(conditions)) {
                    Context.getCurrentScenario().write("SKIPPED BY CONDITIONS");
                    return Void.TYPE;
                }
            }
        }

        logger.debug("NORAUI ConditionedInterceptor invoke method " + invocation.getMethod());
        return invocation.proceed();
    }

    /**
     * Display a message at the beginning of method.
     *
     * @param methodName
     *            is the name of method for logs
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     */
    private void displayMessageAtTheBeginningOfMethod(String methodName, List<GherkinCondition> conditions) {
        logger.debug(String.format("%s with %d contition(s)", methodName, conditions.size()));
        displayConditionsAtTheBeginningOfMethod(conditions);
    }

    /**
     * Display a message at the beginning of method.
     *
     * @param methodName
     *            is the name of method for logs
     * @param field
     *            is the value of parameter
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     */
    protected void displayMessageAtTheBeginningOfMethod(String methodName, String field, List<GherkinCondition> conditions) {
        logger.debug(String.format("%s: %s with %d contition(s)", methodName, field, conditions.size()));
        displayConditionsAtTheBeginningOfMethod(conditions);
    }

    /**
     * Display all conditions at the beginning of method.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     */
    private void displayConditionsAtTheBeginningOfMethod(List<GherkinCondition> conditions) {
        int i = 0;
        for (GherkinCondition gherkinCondition : conditions) {
            i++;
            logger.debug(String.format("  expected contition N°%d=%s", i, gherkinCondition.getExpected()));
            logger.debug(String.format("  actual   contition N°%d=%s", i, gherkinCondition.getActual()));
        }
    }

    /**
     * Check all conditions.
     *
     * @param conditions
     *            list of 'expected' values condition and 'actual' values ({@link noraui.gherkin.GherkinCondition}).
     * @return a boolean
     */
    public boolean checkConditions(List<GherkinCondition> conditions) {
        for (GherkinCondition gherkinCondition : conditions) {
            logger.debug("checkConditions " + gherkinCondition.getActual() + " in context is " + Context.getValue(gherkinCondition.getActual()));
            String actual = Context.getValue(gherkinCondition.getActual()) != null ? Context.getValue(gherkinCondition.getActual()) : gherkinCondition.getActual();
            if (actual == null || !actual.matches("(?i)" + gherkinCondition.getExpected())) {
                return false;
            }
        }
        return true;
    }

}
