package roart.search;

import org.junit.jupiter.api.Test;

import roart.common.config.ConfigConstants;

// base read write
// hbase 23,18 55
// psql   33,32   160
// cass 25,22 771/580
public class DbCopyIT {
    @Test
    public void myTest() throws Exception {
        String src = ConfigConstants.DATABASESPRING;
        String dst = ConfigConstants.DATABASEHBASE;
        Object object;
        //object = new Util(new Sender()).dbcopy(ConfigConstants.DATABASEHIBERNATE , ConfigConstants.DATABASECASSANDRA);
        //object = new Util(new Sender()).dbclear(dst);
        //System.out.println(object);
        object = new Util(new Sender()).dbcopy(src, dst);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(src);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(dst);
        System.out.println(object);
    }

}
