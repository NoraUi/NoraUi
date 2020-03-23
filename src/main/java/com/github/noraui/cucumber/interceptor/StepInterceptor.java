/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import com.github.noraui.Constants;
import com.github.noraui.cucumber.annotation.RetryOnFailure;
import com.github.noraui.cucumber.annotation.RetryOnWarning;
import com.github.noraui.exception.FailureException;
import com.github.noraui.exception.WarningException;
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
                logRunningStep(stepAnnotation, invocation);
            }
        }
        if (m.isAnnotationPresent(RetryOnFailure.class) || m.isAnnotationPresent(RetryOnWarning.class)) {
            RetryOnFailure retryOnFailureAnnotation = null;
            RetryOnWarning retryOnWarningAnnotation = null;
            if (m.isAnnotationPresent(RetryOnFailure.class)) {
                retryOnFailureAnnotation = m.getAnnotation(RetryOnFailure.class);
                if (retryOnFailureAnnotation.verbose()) {
                    log.info("NORAUI StepInterceptor invoke method " + m);
                }
            }
            if (m.isAnnotationPresent(RetryOnWarning.class)) {
                retryOnWarningAnnotation = m.getAnnotation(RetryOnWarning.class);
                if (retryOnWarningAnnotation.verbose()) {
                    log.info("NORAUI StepInterceptor invoke method " + m);
                }
            }
            int i = 0;
            do {
                try {
                    if ((retryOnFailureAnnotation != null && retryOnFailureAnnotation.verbose()) || (retryOnWarningAnnotation != null && retryOnWarningAnnotation.verbose())) {
                        log.info("NORAUI StepInterceptor attempt n° " + i);
                    }
                    return invocation.proceed();
                } catch (FailureException e) {
                    if (retryOnFailureAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor FailureException " + e.getMessage());
                    }
                    if (i == retryOnFailureAnnotation.attempts() - 1) {
                        e.getFailure().fail();
                    }
                    if (retryOnFailureAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor waitting " + retryOnFailureAnnotation.unit().toMillis(retryOnFailureAnnotation.delay()) + " ms");
                    }
                    Thread.sleep(retryOnFailureAnnotation.unit().toMillis(retryOnFailureAnnotation.delay()));
                    if (retryOnFailureAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor wait finished");
                    }
                } catch (WarningException e) {
                    if (retryOnWarningAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor WarningException " + e.getMessage());
                        log.info("NORAUI StepInterceptor waitting " + retryOnWarningAnnotation.unit().toMillis(retryOnWarningAnnotation.delay()) + " ms");
                    }
                    Thread.sleep(retryOnWarningAnnotation.unit().toMillis(retryOnWarningAnnotation.delay()));
                    if (retryOnWarningAnnotation.verbose()) {
                        log.info("NORAUI StepInterceptor wait finished");
                    }
                }
                i++;
            } while ((retryOnFailureAnnotation != null && i < retryOnFailureAnnotation.attempts()) || (retryOnWarningAnnotation != null && i < retryOnWarningAnnotation.attempts()));
        } else {
            try {
                return invocation.proceed();
            } catch (FailureException e) {
                if (Modifier.isPublic(m.getModifiers())) {
                    e.getFailure().fail();
                } else {
                    throw e;
                }
            } catch (WarningException e) {
            }
        }
        return result;
    }

    private void logRunningStep(Annotation stepAnnotation, MethodInvocation invocation)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<? extends Annotation> annotationClass = stepAnnotation.annotationType();
        Method valueMethods = annotationClass.getDeclaredMethod("value");
        Context.goToNextStep();
        final StringBuilder builder = new StringBuilder();
        final String stepIndex = Context.getCurrentSubStepIndex() > 0 ? Context.getCurrentStepIndex() + "." + (Context.getCurrentSubStepIndex() - 1) : String.valueOf(Context.getCurrentStepIndex());
        builder.append("#");
        builder.append(stepIndex);
        builder.append(" - ");
        builder.append(blue(stepAnnotation.annotationType().getSimpleName()));
        builder.append(" ");
        builder.append(String.format(valueMethods.invoke(stepAnnotation).toString().replaceAll("\\{\\S+\\}", yellow("{%s}")).replace("(\\?)", ""), invocation.getArguments()));
        log.info(builder.toString());
    }

    private String blue(String text) {
        return Constants.BLUE + text + Constants.RESET;
    }

    private String yellow(String text) {
        return Constants.YELLOW + text + Constants.RESET;
    }
}
