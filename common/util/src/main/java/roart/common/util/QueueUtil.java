package roart.common.util;

import roart.common.constants.Constants;

public class QueueUtil {
    public static String prefix() {
        String appid = System.getenv(Constants.APPID);
        if (appid != null) {
            return appid;
        } else {
            return "";
        }
    }

    public static String filesetnewQueue(String myid) {
        return prefix() + Constants.FILESETNEWID + myid;
    }

    public static String notfoundsetQueue(String myid) {
        return prefix() + Constants.NOTFOUNDSETID + myid;
    }

    public static String retlistQueue(String myid) {
        return prefix() + Constants.RETLISTID + myid;
    }

    public static String retlistnotQueue(String myid) {
        return prefix() + Constants.RETNOTLISTID + myid;
    }

    public static String filestodoQueue(String myid) {
        return prefix() + Constants.FILESTODOSETID + myid;
    }

    public static String filesdoneQueue(String myid) {
        return prefix() + Constants.FILESDONESETID + myid;
    }

    public static String traversecount(String myid) {
        return prefix() + Constants.TRAVERSECOUNT + myid;
    }

    public static String getListingQueue() {
        return prefix() + Constants.LISTINGQUEUE;
    }

    public static String getTraverseQueue() {
        return prefix() + Constants.TRAVERSEQUEUE;
    }

    public static String getConvertQueue() {
        return prefix() + Constants.CONVERTQUEUE;
    }

    public static String getIndexQueue() {
        return prefix() + Constants.INDEXQUEUE;
    }

    /*
    public static String filesetnewQueue(String myid) {
        return prefix() + Constants.FILESETNEWID + myid;
    }

    public static String filesetnewQueue(String myid) {
        return prefix() + Constants.FILESETNEWID + myid;
    }
    */
}
