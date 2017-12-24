/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.gherkin;

import java.util.HashMap;

@SuppressWarnings("serial")
public class ScenarioRegistry extends HashMap<String, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(Object key) {
        if (containsKey(key)) {
            return super.get(key);
        }
        return null;
    }
}
