package com.github.noraui.cucumber.config;

import java.util.Locale;
import java.util.Map;

import com.github.noraui.gherkin.GherkinStepCondition;

import cucumber.api.TypeRegistry;
import cucumber.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

/*
 * Maps datatables in feature files to custom domain objects.
 */
public class DataTableConfigurer implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry registry) {
        registry.defineDataTableType(new DataTableType(GherkinStepCondition.class, new TableEntryTransformer<GherkinStepCondition>() {
            @Override
            public GherkinStepCondition transform(Map<String, String> entry) {
                return new GherkinStepCondition(entry.get("key"), entry.get("expected"), entry.get("actual"));
            }
        }));
    }

}
