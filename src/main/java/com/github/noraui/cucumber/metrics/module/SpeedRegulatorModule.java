/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.metrics.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulator;
import com.github.noraui.cucumber.metrics.annotation.regulator.SpeedRegulators;
import com.github.noraui.cucumber.metrics.interceptor.SpeedRegulatorInterceptor;
import com.google.inject.Binder;

public class SpeedRegulatorModule extends AbstractMetricsModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeedRegulatorModule.class);

    public static final String SPEED_REGULATOR_ANNOTATION_ENABLE = "SpeedRegulator.annotation.enable";
    public static final String SPEED_REGULATORS_ANNOTATION_ENABLE = "SpeedRegulators.annotation.enable";

    @Override
    public void configure(Binder binder) {
        LOGGER.info("Cucumber Metrics SpeedRegulator configure");

        SpeedRegulatorInterceptor speedRegulatorInterceptor = new SpeedRegulatorInterceptor();
        setAnnotation2Interceptors(binder, SPEED_REGULATOR_ANNOTATION_ENABLE, SpeedRegulator.class, speedRegulatorInterceptor);
        setAnnotation2Interceptors(binder, SPEED_REGULATORS_ANNOTATION_ENABLE, SpeedRegulators.class, speedRegulatorInterceptor);
    }

}
