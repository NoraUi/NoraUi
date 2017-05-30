package noraui.utils;

import java.text.Normalizer;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class NameUtilities {

    public static boolean comparingNames(String name, String otherName) {
        for (String word : Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("'", " ").replaceAll("-", " ").replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim().split(" ")) {
            if (!StringUtils.containsIgnoreCase(
                    Normalizer.normalize(otherName, Normalizer.Form.NFD).replaceAll("'", " ").replaceAll("-", " ").replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim(), word)) {
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
}
