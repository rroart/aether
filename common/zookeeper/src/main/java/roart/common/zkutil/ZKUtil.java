package roart.common.zkutil;

import roart.common.constants.Constants;

public class ZKUtil {

    public static String appid() {
        String appid = System.getenv(Constants.APPID);
        if (appid != null) {
            return appid;
        } else {
            return "";
        }
    }

    public static String getPath(String elem) {
        return "/" + Constants.AETHER + appid() + "/" + elem + "/";
    }

    public static String getPath() {
        return "/" + Constants.AETHER + appid() + "/";
    }

    public static String getPath0() {
        return "/" + Constants.AETHER + appid();
    }
}
