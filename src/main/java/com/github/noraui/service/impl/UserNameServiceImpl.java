/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service.impl;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.github.noraui.service.UserNameService;
import com.google.inject.Singleton;

@Singleton
public class UserNameServiceImpl implements UserNameService {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean comparingNames(String name, String otherName) {
        for (String word : getNormalizeName(name).split(" ")) {
            if (!StringUtils.containsIgnoreCase(getNormalizeName(otherName).trim(), word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findOptionByIgnoreCaseText(String text, Select dropDown) {
        int index = 0;
        for (WebElement option : dropDown.getOptions()) {
            if (comparingNames(text, option.getText())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNormalizeName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD).replace("'", " ").replace("-", " ").replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim();
    }

}
