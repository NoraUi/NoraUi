package com.github.noraui.cucumber.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.exception.FailureException;
import com.github.noraui.utils.Context;

import cucumber.runtime.java.StepDefAnnotation;
import javassist.Modifier;

public class StepInterceptor implements MethodInterceptor {

    /**
     * Specific logger
     */
    private static final Logger logger = LoggerFactory.getLogger(StepInterceptor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Object result = null;
        Method m = invocation.getMethod();
        Annotation[] annotations = m.getAnnotations();
        if (annotations.length > 0) {
            Annotation stepAnnotation = annotations[annotations.length - 1];
            for (Annotation a : annotations) {
                if (a.annotationType().getName().startsWith("cucumber.api.java." + Context.getLocale().getLanguage())) {
                    stepAnnotation = a;
                    break;
                }
            }
            if (stepAnnotation.annotationType().isAnnotationPresent(StepDefAnnotation.class)) {
                Matcher matcher = Pattern.compile("value=(.*)\\)").matcher(stepAnnotation.toString());
                if (matcher.find()) {
                    logger.info(
                            "> " + stepAnnotation.annotationType().getSimpleName() + " " + String.format(matcher.group(1).replace("(.*)", "%s").replace("[\\.|\\?]", ""), invocation.getArguments()));
                }
            }
        }
        if (m.isAnnotationPresent(RetryOnFailure.class)) {
            RetryOnFailure retryAnnotation = m.getAnnotation(RetryOnFailure.class);
            if (retryAnnotation.verbose()) {
                logger.info("NORAUI StepInterceptor invoke method " + m);
            }
            for (int i = 0; i < retryAnnotation.attempts(); i++) {
                try {
                    if (retryAnnotation.verbose()) {
                        logger.info("NORAUI StepInterceptor attempt n° " + i);
                    }
                    return invocation.proceed();
                } catch (FailureException e) {
                    if (retryAnnotation.verbose()) {
                        logger.info("NORAUI StepInterceptor Exception " + e.getMessage());
                    }
                    if (i == retryAnnotation.attempts() - 1) {
                        e.getFailure().fail();
                    }
                    Thread.sleep(retryAnnotation.unit().toMillis(retryAnnotation.delay()));
                }
            }
        } else {
            try {
                return invocation.proceed();
            } catch (FailureException e) {
                if (Modifier.isPublic(m.getModifiers())) {
                    e.getFailure().fail();
                } else {
                    throw e;
                }
            }
        }
        return result;
    }
}
