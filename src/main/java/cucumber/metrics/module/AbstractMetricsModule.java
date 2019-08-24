/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.metrics.module;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;

public abstract class AbstractMetricsModule implements Module {

    /**
     * Specific LOGGER
     */
    private final Logger LOGGER = LoggerFactory.getLogger(AbstractMetricsModule.class);

    void setAnnotation2Interceptors(Binder binder, String annotationEnable, final Class<? extends Annotation> annotationType, MethodInterceptor... interceptors) {
        String ae = System.getenv(annotationEnable);
        if ("false".equals(ae)) {
            LOGGER.info(annotationEnable + " set to false.");
        } else if ("true".equals(ae)) {
            LOGGER.info(annotationEnable + " set to true.");
            binder.bindInterceptor(any(), annotatedWith(annotationType), interceptors);
        } else {
            LOGGER.info(annotationEnable + " not set or wrong set (set to false by default).");
        }
    }

}
