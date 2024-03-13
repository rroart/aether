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
        return prefix() + Constants.FILESETNEWQUEUE + myid;
    }

    public static String notfoundsetQueue(String myid) {
        return prefix() + Constants.NOTFOUNDSETQUEUE + myid;
    }

    public static String retlistQueue(String myid) {
        return prefix() + Constants.RETLISTQUEUE + myid;
    }

    public static String retlistnotQueue(String myid) {
        return prefix() + Constants.RETNOTLISTQUEUE + myid;
    }

    public static String filestodoQueue(String myid) {
        return prefix() + Constants.FILESTODOSETQUEUE + myid;
    }

    public static String filesdoneQueue(String myid) {
        return prefix() + Constants.FILESDONESETQUEUE + myid;
    }

    public static String deletedQueue(String myid) {
        return prefix() + Constants.DELETEDQUEUE + myid;
    }

    public static String changedQueue(String myid) {
        return prefix() + Constants.CHANGEDQUEUE + myid;
    }

    public static String notconvertedQueue(String myid) {
        return prefix() + Constants.UNCONVERTEDQUEUE + myid;
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

    public static String getTaskMap() {
        return prefix() + Constants.TASKMAP;
    }

    public static String getResultMap() {
        return prefix() + Constants.RESULTMAP;
    }

    public static String getTraverseCountMap() {
        return prefix() + Constants.TRAVERSECOUNTMAP;
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
