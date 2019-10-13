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

import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

/*
 * Maps datatables in feature files to custom domain objects.
 */
@Loggable
public class DataTableConfigurer implements TypeRegistryConfigurer {

    static Logger log;

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
                log.debug("configureTypeRegistry GherkinStepCondition key:{}", entry.get("key"));
                log.debug("configureTypeRegistry GherkinStepCondition expected:{}", entry.get("expected"));
                log.debug("configureTypeRegistry GherkinStepCondition actual:{}", entry.get("actual"));
                return new GherkinStepCondition(entry.get("key"), entry.get("expected") != null ? entry.get("expected") : "", entry.get("actual") != null ? entry.get("actual") : "");
            }
        }));
        registry.defineDataTableType(new DataTableType(GherkinConditionedLoopedStep.class, new TableEntryTransformer<GherkinConditionedLoopedStep>() {
            @Override
            public GherkinConditionedLoopedStep transform(Map<String, String> entry) {
                log.debug("configureTypeRegistry GherkinConditionedLoopedStep key:{}", entry.get("key"));
                log.debug("configureTypeRegistry GherkinConditionedLoopedStep step:{}", entry.get("step"));
                log.debug("configureTypeRegistry GherkinConditionedLoopedStep expected:{}", entry.get("expected"));
                log.debug("configureTypeRegistry GherkinConditionedLoopedStep actual:{}", entry.get("actual"));
                return new GherkinConditionedLoopedStep(entry.get("key"), entry.get("step"), entry.get("expected") != null ? entry.get("expected") : "",
                        entry.get("actual") != null ? entry.get("actual") : "");
            }
        }));
    }

}
