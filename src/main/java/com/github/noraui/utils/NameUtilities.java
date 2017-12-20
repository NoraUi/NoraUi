/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.utils;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class NameUtilities {

    private NameUtilities() {
    }

    public static boolean comparingNames(String name, String otherName) {
        for (String word : getNormalizeName(name).split(" ")) {
            if (!StringUtils.containsIgnoreCase(getNormalizeName(otherName).trim(), word)) {
                return false;
            }
        }
        return true;
    }

    public static int findOptionByIgnoreCaseText(String text, Select dropDown) {
        int index = 0;
        for (WebElement option : dropDown.getOptions()) {
            if (comparingNames(text, option.getText())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static String getNormalizeName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("'", " ").replaceAll("-", " ").replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim();
    }
}
