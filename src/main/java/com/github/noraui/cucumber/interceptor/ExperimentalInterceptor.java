/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.annotation.Experimental;

public class ExperimentalInterceptor implements MethodInterceptor {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentalInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //
        Method m = invocation.getMethod();
        if (m.isAnnotationPresent(Experimental.class)) {
            Experimental experimentalAnnotation = m.getAnnotation(Experimental.class);
            LOGGER.warn("CAUTION: You use a experimental generic step named [{}].", experimentalAnnotation.name());
        }
        LOGGER.debug("NORAUI ExperimentalInterceptor invoke method {}", invocation.getMethod());
        return invocation.proceed();
    }

}
