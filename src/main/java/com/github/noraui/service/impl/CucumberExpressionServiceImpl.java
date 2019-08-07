/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.github.noraui.service.CucumberExpressionService;
import com.google.inject.Singleton;

import io.cucumber.cucumberexpressions.Argument;
import io.cucumber.cucumberexpressions.CucumberExpression;
import io.cucumber.cucumberexpressions.Expression;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

@Singleton
public class CucumberExpressionServiceImpl implements CucumberExpressionService {
    
    @Override
    public List<?> match(String expressionString, String text) {
        Expression expression;
        ParameterTypeRegistry parameterTypeRegistry = new ParameterTypeRegistry(Locale.ENGLISH);
        expression = new CucumberExpression(expressionString, parameterTypeRegistry);
        List<Argument<?>> args = expression.match(text);
        if (args == null) {
            return null;
        } else {
            List<Object> list = new ArrayList<>();
            for (Argument<?> arg : args) {
                Object value = arg.getValue();
                list.add(value);
            }
            return list;
        }
    }

}
