/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import java.util.List;

public interface CucumberExpressionService {

    List<?> match(String expressionString, String text);
    
}
