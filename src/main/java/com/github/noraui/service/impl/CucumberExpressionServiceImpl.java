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

import org.apiguardian.api.API;

import com.github.noraui.service.CucumberExpressionService;
import com.google.inject.Singleton;

import io.cucumber.cucumberexpressions.Argument;
import io.cucumber.cucumberexpressions.Expression;
import io.cucumber.cucumberexpressions.ExpressionFactory;
import io.cucumber.cucumberexpressions.ParameterTypeRegistry;

@API(status = API.Status.STABLE)
@Singleton
public class CucumberExpressionServiceImpl implements CucumberExpressionService {

    @Override
    public List<Object> match(String expressionString, String text) {
        Expression expression;
        ParameterTypeRegistry parameterTypeRegistry = new ParameterTypeRegistry(Locale.ENGLISH);
        expression = new ExpressionFactory(parameterTypeRegistry).createExpression(expressionString);
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
