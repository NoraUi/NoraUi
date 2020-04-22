/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.gherkin;

public enum Inequality {

    SUPERIOR(">"), INFERIOR("<"), SUPERIOR_OR_EQUALS(">="), INFERIOR_OR_EQUALS("<="), EQUALS("=="), NOT_EQUALS("!=");

    private String value;

    Inequality(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Inequality findBySymbol(String Symbol) {
        for (Inequality inequality : values()) {
            if (inequality.value.equals(Symbol)) {
                return inequality;
            }
        }
        return null;
    }

}
