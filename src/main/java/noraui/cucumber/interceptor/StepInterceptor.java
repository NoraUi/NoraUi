package noraui.cucumber.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import cucumber.runtime.java.StepDefAnnotation;
import javassist.Modifier;
import noraui.cucumber.annotation.RetryOnFailure;
import noraui.exception.FailureException;

public class StepInterceptor implements MethodInterceptor {

    private static Logger logger = Logger.getLogger(StepInterceptor.class.getName());

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
            if (stepAnnotation.annotationType().isAnnotationPresent(StepDefAnnotation.class)) {
                Matcher matcher = Pattern.compile("value=(.*)\\)").matcher(stepAnnotation.toString());
                if (matcher.find()) {
                    logger.info("> " + stepAnnotation.annotationType().getSimpleName() + " " + String.format(matcher.group(1).replace("(.*)", "%s"), invocation.getArguments()));
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
