/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.metrics.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Inject;

import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.Times;
import cucumber.metrics.interceptor.TimeInterceptor;

public class TimeModule extends AbstractMetricsModule {

    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeModule.class.getName());

    public static final String TIME_ANNOTATION_ENABLE = "Time.annotation.enable";
    public static final String TIMES_ANNOTATION_ENABLE = "Times.annotation.enable";

    @Inject
    TimeInterceptor timeInterceptor;

    @Override
    public void configure(Binder binder) {
        LOGGER.info("Cucumber Metrics Time configure");

        // TimeInterceptor timeInterceptor = new TimeInterceptor();
        setAnnotation2Interceptors(binder, TIME_ANNOTATION_ENABLE, Time.class, timeInterceptor);
        setAnnotation2Interceptors(binder, TIMES_ANNOTATION_ENABLE, Times.class, timeInterceptor);
    }

}
