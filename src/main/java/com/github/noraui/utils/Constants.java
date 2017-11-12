package com.github.noraui.utils;

public class Constants {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_REG_EXP = "(^(((0[1-9]|1[0-9]|2[0-8])[\\/](0[1-9]|1[012]))|((29|30|31)[\\/](0[13578]|1[02]))|((29|30)[\\/](0[4,6,9]|11)))[\\/](19|[2-9][0-9])\\d\\d$)|(^29[\\/]02[\\/](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";

    public static final String VALUE = "value";
    public static final String ALERT_KEY = "Alert:";

    public static final String DEFAULT_ENDODING = "UTF-8";

    public static final String IS_CONNECTED_REGISTRY_KEY = "AUTH-isConnected";

    public static final String DATA_IN = "/data/in/";
    public static final String DATA_OUT = "/data/out/";

    /**
     * Private constructor
     */
    private Constants() {
    }
}
