/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import java.util.List;

public interface CucumberExpressionService {

    /**
     * 
     * @param expressionString source in cucumber annotation.
     * @param text present in Gherkin file.
     * @return list of arguments (can be 0) if match OR null if no match.
     */
    List<Object> match(String expressionString, String text);

}
