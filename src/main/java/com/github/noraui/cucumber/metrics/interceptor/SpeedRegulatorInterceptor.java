/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.metrics.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulator;
import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulators;
import com.github.noraui.cucumber.metrics.core.impl.Meter;
import com.github.noraui.log.annotation.Loggable;

@Loggable
public class SpeedRegulatorInterceptor implements MethodInterceptor {

    static Logger LOGGER;

    private final ConcurrentMap<String, Meter> speedometers = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        boolean verbose = false;
        //
        Method m = invocation.getMethod();

        //
        if (m.isAnnotationPresent(SpeedRegulator.class)) {
            SpeedRegulator annotation = m.getAnnotation(SpeedRegulator.class);
            speedLimiter(annotation);
            verbose = annotation.verbose();
        }
        if (m.isAnnotationPresent(SpeedRegulators.class)) {
            SpeedRegulators annotations = m.getAnnotation(SpeedRegulators.class);
            for (int i = 0; i < annotations.value().length; i++) {
                SpeedRegulator annotation = annotations.value()[i];
                speedLimiter(annotation);
                if (!verbose) {
                    verbose = annotation.verbose();
                }
            }
        }

        //
        if (verbose) {
            LOGGER.info("Cucumber Metrics SpeedRegulatorInterceptor invoke method " + invocation.getMethod() + " is called on " + invocation.getThis() + " with args " + invocation.getArguments());
        }
        Object result = invocation.proceed();
        if (verbose) {
            LOGGER.info("method " + invocation.getMethod() + " returns " + result);
        }
        return result;
    }

    private void speedLimiter(SpeedRegulator annotation) {
        if (annotation.cost() != -1 || annotation.costString().startsWith("${") && annotation.costString().endsWith("}")) {
            int cost = annotation.cost();
            if (annotation.costString().startsWith("${") && annotation.costString().endsWith("}")) {
                String costPropertie = System.getProperty(annotation.costString().substring(2, annotation.costString().length() - 1));
                if (costPropertie != null && costPropertie.matches("\\d")) {
                    cost = Integer.parseInt(costPropertie);
                }
            }
            if (annotation.verbose()) {
                LOGGER.info(annotation.application() + " cost " + cost + " " + annotation.unit());
            }
            Meter meter = speedometers.containsKey(annotation.application()) ? speedometers.get(annotation.application()) : new Meter(annotation.unit().toNanos(cost));
            meter.waitIfNecessaryAndUpdateNextAvailableTime();
            speedometers.put(annotation.application(), meter);
        }
    }

}