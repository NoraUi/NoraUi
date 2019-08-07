/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package cucumber.metrics.interceptor;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import cucumber.metrics.annotation.time.Time;
import cucumber.metrics.annotation.time.TimeName;
import cucumber.metrics.annotation.time.TimeValue;
import cucumber.metrics.annotation.time.Times;
import cucumber.metrics.core.impl.Meter;
import cucumber.metrics.jmx.TimedJmxDynamicMBean;

@Singleton
public class TimeInterceptor implements MethodInterceptor {

    /**
     * Specific LOGGER
     */
    private final Logger LOGGER = LoggerFactory.getLogger(TimeInterceptor.class);
    
    private final ConcurrentMap<String, Meter> meters = new ConcurrentHashMap<>();
    private TimedJmxDynamicMBean mbean = null;

    public TimeInterceptor() {
        this.mbean = new TimedJmxDynamicMBean();
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.registerMBean(this.mbean, new ObjectName("cucumber.metrics.jmx:type=TimedJmxDynamicMBean"));
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.warn("TimedInterceptor Exception - InstanceAlreadyExistsException" + e);
        } catch (MBeanRegistrationException e) {
            LOGGER.warn("TimedInterceptor Exception - MBeanRegistrationException" + e);
        } catch (NotCompliantMBeanException e) {
            LOGGER.warn("TimedInterceptor Exception - NotCompliantMBeanException" + e);
        } catch (MalformedObjectNameException e) {
            LOGGER.warn("TimedInterceptor Exception - MalformedObjectNameException" + e);
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //
        Method m = invocation.getMethod();
        Annotation[][] as = m.getParameterAnnotations();
        Object[] args = invocation.getArguments();

        //
        if (m.isAnnotationPresent(Time.class)) {
            Time timeAnnotation = m.getAnnotation(Time.class);
            timeProceed(m, as, args, timeAnnotation);
        }
        if (m.isAnnotationPresent(Times.class)) {
            Times annotations = m.getAnnotation(Times.class);
            for (int i = 0; i < annotations.value().length; i++) {
                Time timeAnnotation = annotations.value()[i];
                timeProceed(m, as, args, timeAnnotation);
            }
        }

        //
        LOGGER.debug("Cucumber Metrics TimedInterceptor invoke method " + invocation.getMethod() + " is called on " + invocation.getThis() + " with args " + invocation.getArguments());
        Object result = invocation.proceed();
        LOGGER.debug("method " + invocation.getMethod() + " returns " + result);
        return result;
    }

    private void timeProceed(Method m, Annotation[][] as, Object[] args, Time timeAnnotation) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        String timedName = getTimeName(m, as, args, timeAnnotation);
        int timedMark = getTimeMark(as, args, timeAnnotation);
        if (timeAnnotation.verbose()) {
            LOGGER.debug("Timed name:" + timedName + "  Timed mark:" + timedMark);
        }

        timed(timedName, timedMark);
        if (timeAnnotation.verbose()) {
            LOGGER.debug("Timed of :" + timedName + " is " + meters.get(timedName).getCount());
        }

        // JMX
        if (timeAnnotation.jmx()) {
            mbean.setAttribute(new Attribute(timedName, meters.get(timedName).getCount()));
        }
    }

    private void timed(String timedName, int timedMark) {
        Meter meter = meters.containsKey(timedName) ? meters.get(timedName) : new Meter();
        meter.mark(timedMark);
        meters.put(timedName, meter);
    }

    private int getTimeMark(Annotation[][] as, Object[] args, Time timeAnnotation) {
        int timedValue = 1;
        if (timeAnnotation.mark() == 1) {
            for (int i = 0; i < as.length; i++) {
                Annotation[] annotations = as[i];
                for (Annotation annotation2 : annotations) {
                    if (annotation2 instanceof TimeValue && args[i] instanceof Integer) {
                        timedValue = (Integer) args[i];
                    }
                }
            }
        }
        return timedValue;
    }

    private String getTimeName(Method m, Annotation[][] as, Object[] args, Time timeAnnotation) {
        String timedName = "";
        if ("".equals(timeAnnotation.name())) {
            timedName = m.getName();
        } else {
            if (timeAnnotation.name().startsWith("{") && timeAnnotation.name().endsWith("}")) {
                for (int i = 0; i < as.length; i++) {
                    Annotation[] annotations = as[i];
                    for (Annotation annotation2 : annotations) {
                        if (annotation2 instanceof TimeName && timeAnnotation.name().substring(1, timeAnnotation.name().length() - 1).equals(((TimeName) annotation2).value())
                                && args[i] instanceof String) {
                            timedName = (String) args[i];
                        }
                    }
                }
            } else {
                timedName = timeAnnotation.name();
            }
        }
        return timedName;
    }

}
