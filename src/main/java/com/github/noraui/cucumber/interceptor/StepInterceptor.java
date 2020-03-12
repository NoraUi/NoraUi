/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.exception.FailureException;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Context;

import cucumber.runtime.java.StepDefAnnotation;
import javassist.Modifier;

@Loggable
public class StepInterceptor implements MethodInterceptor {

    static Logger log;

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
                if (a.annotationType().getName().startsWith("io.cucumber.java." + Context.getLocale().getLanguage())) {
                    stepAnnotation = a;
                    break;
                }
            }
            if (stepAnnotation.annotationType().isAnnotationPresent(StepDefAnnotation.class)) {
                Matcher matcher = Pattern.compile("value=(.*)\\)").matcher(stepAnnotation.toString());
                if (matcher.find()) {
                    log.info("---> {} " + matcher.group(1).replaceAll("\\{\\S+\\}", "{%s}").replace("(\\?)", ""), stepAnnotation.annotationType().getSimpleName(), invocation.getArguments());
                }
            }
        }
        if (m.isAnnotationPresent(RetryOnFailure.class)) {
            RetryOnFailure retryAnnotation = m.getAnnotation(RetryOnFailure.class);
            if (retryAnnotation.verbose()) {
                log.info("NORAUI StepInterceptor invoke method " + m);
            }
            for (int i = 0; i < retryAnnotation.attempts(); i++) {
                try {
                    if (retryAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor attempt n° " + i);
                    }
                    return invocation.proceed();
                } catch (FailureException e) {
                    if (retryAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor Exception " + e.getMessage());
                    }
                    if (i == retryAnnotation.attempts() - 1) {
                        e.getFailure().fail();
                    }
                    if (retryAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor waitting " + retryAnnotation.unit().toMillis(retryAnnotation.delay()) + " ms");
                    }
                    Thread.sleep(retryAnnotation.unit().toMillis(retryAnnotation.delay()));
                    if (retryAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor wait finished");
                    }
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
