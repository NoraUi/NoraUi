/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cucumber.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;

import com.github.noraui.application.page.Page;
import com.github.noraui.application.page.Page.PageElement;
import com.github.noraui.exception.TechnicalException;
import com.github.noraui.gherkin.GherkinConditionedLoopedStep;
import com.github.noraui.gherkin.GherkinStepCondition;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.utils.Messages;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterType;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

/*
 * Maps datatables in feature files to custom domain objects.
 */
@Loggable
public class CucumberTypeRegistryConfigurer implements TypeRegistryConfigurer {

    static Logger log;

    private static List<DataTableType> dataTableTypes = Collections.emptyList();
    private static List<ParameterType<?>> parameterTypes = Collections.emptyList();

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry registry) {
        getDataTableTypes().stream().forEach(registry::defineDataTableType);
        getParameterTypes().stream().forEach(registry::defineParameterType);
    }

    public static List<DataTableType> getDataTableTypes() {
        if (dataTableTypes.isEmpty()) {
            dataTableTypes = Arrays.asList(new DataTableType(Boolean.class, Boolean::parseBoolean), new DataTableType(GherkinStepCondition.class, new TableEntryTransformer<GherkinStepCondition>() {
                @Override
                public GherkinStepCondition transform(Map<String, String> entry) {
                    log.debug("configureTypeRegistry GherkinStepCondition key:{}", entry.get("key"));
                    log.debug("configureTypeRegistry GherkinStepCondition expected:{}", entry.get("expected"));
                    log.debug("configureTypeRegistry GherkinStepCondition actual:{}", entry.get("actual"));
                    return new GherkinStepCondition(entry.get("key"), entry.get("expected") != null ? entry.get("expected") : "", entry.get("actual") != null ? entry.get("actual") : "");
                }
            }), new DataTableType(GherkinConditionedLoopedStep.class, new TableEntryTransformer<GherkinConditionedLoopedStep>() {
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
        return dataTableTypes;
    }

    public static List<ParameterType<?>> getParameterTypes() {
        if (parameterTypes.isEmpty()) {
            parameterTypes = Arrays.asList(new ParameterType<PageElement>("page-element", "\\'(.*\\..*-.*)\\'", PageElement.class, (final String strPageElement) -> {
                String page = null, elementName = null;
                try {
                    page = strPageElement.split("-")[0];
                    elementName = strPageElement.split("-")[1];
                    return Page.getInstance(page).getPageElementByKey('-' + elementName);
                } catch (Exception e) {
                    throw new TechnicalException(Messages.format(Messages.getMessage(Page.UNABLE_TO_RETRIEVE_PAGE_ELEMENT), page, elementName), e);
                }
            }), new ParameterType<Page>("page", "\\'(.*\\..*)\\'", Page.class, (final String strPage) -> {
                try {
                    return Page.getInstance(strPage);
                } catch (Exception e) {
                    throw new TechnicalException(Messages.format(Messages.getMessage(Page.UNABLE_TO_RETRIEVE_PAGE), strPage), e);
                }
            }), new ParameterType<Boolean>("is-isnot", "(is( not)?)|(est|n\\'est pas)", Boolean.class, (final String not) -> {
                return Boolean.valueOf(not.split(" ").length > 1);
            }), new ParameterType<Boolean>("should-shouldnot", "(should( not)?)|(devrait|devraient|ne devrait pas|ne devraient pas)", Boolean.class, (final String not) -> {
                return Boolean.valueOf(not.split(" ").length > 1);
            }));
        }
        return parameterTypes;
    }
}
