/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.github.noraui.cucumber.config.CucumberTypeRegistryConfigurer;
import com.github.noraui.log.annotation.Loggable;
import com.github.noraui.service.CucumberExpressionService;
import com.google.inject.Singleton;

import io.cucumber.cucumberexpressions.Argument;
import io.cucumber.cucumberexpressions.CucumberExpression;
import io.cucumber.cucumberexpressions.Expression;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

@Loggable
@Singleton
public class CucumberExpressionServiceImpl implements CucumberExpressionService {

    static Logger log;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> match(String expressionString, String text) {
        log.debug("CucumberExpressionService match expressionString: {} and text: {}", expressionString, text);
        Expression expression;
        ParameterTypeRegistry parameterTypeRegistry = new ParameterTypeRegistry(Locale.ENGLISH);
        CucumberTypeRegistryConfigurer.getParameterTypes().stream().forEach(parameterTypeRegistry::defineParameterType);
        expression = new CucumberExpression(expressionString, parameterTypeRegistry);
        List<Argument<?>> args = expression.match(text);
        return args == null ? null : args.stream().map(Argument::getValue).collect(Collectors.toList());
    }

}
