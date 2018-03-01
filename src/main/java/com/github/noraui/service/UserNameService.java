/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import org.openqa.selenium.support.ui.Select;

public interface UserNameService {

    /**
     * @param name
     * @param otherName
     * @return
     */
    boolean comparingNames(String name, String otherName);

    /**
     * @param text
     * @param dropDown
     * @return
     */
    int findOptionByIgnoreCaseText(String text, Select dropDown);

    /**
     * @param name
     * @return
     */
    String getNormalizeName(String name);

}
