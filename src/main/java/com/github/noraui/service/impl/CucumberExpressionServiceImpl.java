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

import com.github.noraui.service.CucumberExpressionService;
import com.google.inject.Singleton;

import io.cucumber.cucumberexpressions.Argument;
import io.cucumber.cucumberexpressions.CucumberExpression;
import io.cucumber.cucumberexpressions.Expression;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

@Singleton
public class CucumberExpressionServiceImpl implements CucumberExpressionService {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> match(String expressionString, String text) {
        Expression expression;
        ParameterTypeRegistry parameterTypeRegistry = new ParameterTypeRegistry(Locale.ENGLISH);
        expression = new CucumberExpression(expressionString, parameterTypeRegistry);
        List<Argument<?>> args = expression.match(text);
        return args == null ? null : args.stream().map(arg -> arg.getValue()).collect(Collectors.toList());
    }

}
