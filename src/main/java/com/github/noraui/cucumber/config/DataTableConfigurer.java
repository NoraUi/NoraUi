/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.config;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

/*
 * Maps datatables in feature files to custom domain objects.
 */
public class DataTableConfigurer implements TypeRegistryConfigurer {
    
    /**
     * Specific LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTableConfigurer.class);

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry registry) {
        registry.defineDataTableType(new DataTableType(Boolean.class, Boolean::parseBoolean));
        registry.defineDataTableType(new DataTableType(GherkinStepCondition.class, new TableEntryTransformer<GherkinStepCondition>() {
            @Override
            public GherkinStepCondition transform(Map<String, String> entry) {
                return new GherkinStepCondition(entry.get("key"), entry.get("expected"), entry.get("actual"));
            }
        }));
        registry.defineDataTableType(new DataTableType(GherkinConditionedLoopedStep.class, new TableEntryTransformer<GherkinConditionedLoopedStep>() {
            @Override
            public GherkinConditionedLoopedStep transform(Map<String, String> entry) {
                LOGGER.info("configureTypeRegistry GherkinConditionedLoopedStep key:{}", entry.get("key"));
                LOGGER.info("configureTypeRegistry GherkinConditionedLoopedStep step:{}", entry.get("step"));
                LOGGER.info("configureTypeRegistry GherkinConditionedLoopedStep expected:{}", entry.get("expected"));
                LOGGER.info("configureTypeRegistry GherkinConditionedLoopedStep actual:{}", entry.get("actual"));
                return new GherkinConditionedLoopedStep(entry.get("key"), entry.get("step"), entry.get("expected"), entry.get("actual"));
            }
        }));
    }

}
