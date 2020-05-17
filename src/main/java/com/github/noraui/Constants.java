/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 *
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @since 4.2.0
 */
public class Constants {

    /**
     * @deprecated As of release 4.1.4, replaced by {@link #FRENCH_DATE_FORMAT}
     *             French date format.
     */
    @Deprecated
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     * @deprecated As of release 4.1.4, replaced by {@link #FRENCH_DATE_FORMAT_REG_EXP}
     *             Regex to validate date format dd/mm/yyyy.
     */
    @Deprecated
    public static final String DATE_FORMAT_REG_EXP = "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";

    /**
     * French date format.
     */
    public static final String FRENCH_DATE_FORMAT = "dd/MM/yyyy";

    /**
     * Regex to validate date format dd/mm/yyyy.
     */
    public static final String FRENCH_DATE_FORMAT_REG_EXP = "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";

    public static final String VALUE = "value";

    public static final String PREFIX_SAVE = "SAVE ";

    public static final Charset DEFAULT_ENDODING = StandardCharsets.UTF_8;

    public static final String DEFAULT_ZONE_ID = "Europe/London";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_REG_EXP = "(^(19|[2-9][0-9])\\d\\d[\\-](((0[1-9]|1[012]))[\\-](0[1-9]|1[0-9]|2[0-8])|((0[13578]|1[02]))[\\-](29|30|31)|((0[4,6,9]|11)[\\-](29|30)))$)|(^(19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)[\\-]02[\\-]29$)";

    public static final String USER_DIR = "user.dir";
    public static final String CLI_FILES_DIR = ".noraui";
    public static final String CLI_APPLICATIONS_FILES_DIR = "applications";
    public static final String CLI_SCENARIOS_FILES_DIR = "scenarios";

    public static final String SCENARIO_FILE = "scenarios.properties";

    public static final String IS_CONNECTED_REGISTRY_KEY = "AUTH-isConnected";

    public static final String DATA_IN = "/data/in/";
    public static final String DATA_OUT = "/data/out/";

    public static final String DOWNLOADED_FILES_FOLDER = "downloadFiles";
    public static final String EXPECTED_FILES_FOLDER = "expectedFiles";

    public static final String TOP_LEVEL_PACKAGE = Constants.class.getPackage().getName();

    // Reset
    public static final String RESET = "\u001B[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\u001B[30m"; // BLACK
    public static final String RED = "\u001B[31m"; // RED
    public static final String GREEN = "\u001B[32m"; // GREEN
    public static final String YELLOW = "\u001B[33m"; // YELLOW
    public static final String BLUE = "\u001B[34m"; // BLUE
    public static final String PURPLE = "\u001B[35m"; // PURPLE
    public static final String CYAN = "\u001B[36m"; // CYAN
    public static final String WHITE = "\u001B[37m"; // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\u001B[90m"; // BLACK
    public static final String RED_BRIGHT = "\u001B[91m"; // RED
    public static final String GREEN_BRIGHT = "\u001B[92m"; // GREEN
    public static final String YELLOW_BRIGHT = "\u001B[93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\u001B[94m"; // BLUE
    public static final String PURPLE_BRIGHT = "\u001B[95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\u001B[96m"; // CYAN
    public static final String WHITE_BRIGHT = "\u001B[97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\u001B[100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\u001B[101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\u001B[102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\u001B[103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\u001B[104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\u001B[105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\u001B[106m"; // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\u001B[107m"; // WHITE

    /**
     * Private constructor
     */
    private Constants() {
    }
}
