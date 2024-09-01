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
        return Constants.FILESETNEWQUEUE + prefix() + myid;
    }

    public static String notfoundsetQueue(String myid) {
        return Constants.NOTFOUNDSETQUEUE + prefix() + myid;
    }

    public static String retlistQueue(String myid) {
        return Constants.RETLISTQUEUE + prefix() + myid;
    }

    public static String retlistnotQueue(String myid) {
        return Constants.RETNOTLISTQUEUE + prefix() + myid;
    }

    public static String filestodoQueue(String myid) {
        return Constants.FILESTODOSETQUEUE + prefix() + myid;
    }

    public static String filesdoneQueue(String myid) {
        return Constants.FILESDONESETQUEUE + prefix() + myid;
    }

    public static String deletedQueue(String myid) {
        return Constants.DELETEDQUEUE + prefix() + myid;
    }

    public static String changedQueue(String myid) {
        return Constants.CHANGEDQUEUE + prefix() + myid;
    }

    public static String notconvertedQueue(String myid) {
        return Constants.UNCONVERTEDQUEUE + prefix() + myid;
    }

    public static String traversedSet(String myid) {
        return Constants.TRAVERSEDSET + prefix() + myid;
    }

    public static String traversecount(String myid) {
        return Constants.TRAVERSECOUNT + prefix() + myid;
    }

    public static String getListingQueue() {
        return Constants.LISTINGQUEUE + prefix();
    }

    public static String getTraverseQueue() {
        return Constants.TRAVERSEQUEUE + prefix();
    }

    public static String getConvertQueue() {
        return Constants.CONVERTQUEUE + prefix();
    }

    public static String getIndexQueue() {
        return Constants.INDEXQUEUE + prefix();
    }

    public static String getTaskMap() {
        return Constants.TASKMAP + prefix();
    }

    public static String getResultMap() {
        return Constants.RESULTMAP + prefix();
    }

    public static String getTraverseCountMap() {
        return Constants.TRAVERSECOUNTMAP + prefix();
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
