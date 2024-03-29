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

    public static String convertappid() {
        String appid = System.getenv(Constants.CONVERTAPPID);
        if (appid != null) {
            return appid;
        } else {
            return "";
        }
    }

    public static String getAppidPath(String elem) {
        return "/" + Constants.AETHER + appid() + "/" + elem + "/";
    }

    public static String getAppidPath() {
        return "/" + Constants.AETHER + appid() + "/";
    }

    public static String getAppidPath0() {
        return "/" + Constants.AETHER + appid();
    }

    public static String getCommonPath() {
        return "/" + Constants.AETHER + "/";
    }
    
    public static String getCommonPath(String elem) {
        return "/" + Constants.AETHER + "/" + elem + "/";
    }
    
    public static String getCommonPath0() {
        return "/" + Constants.AETHER;
    }

    public static boolean useCommon() {
        return "".equals(appid()) || !convertappid().equals(appid());
    }

    public static String getCommonPathIfCommon(String elem) {
        if (useCommon()) {
            return getCommonPath(elem);
        } else {
            return getAppidPath(elem);
        }
    }
    
}
