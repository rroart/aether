package roart.search;

import org.junit.jupiter.api.Test;

import roart.common.config.ConfigConstants;

public class DbCopyIT {
    @Test
    public void myTest() throws Exception {
        String src = ConfigConstants.DATABASEHIBERNATE;
        String dst = ConfigConstants.DATABASEHBASE;
        Object object;
        //object = new Util(new Sender()).dbcopy(ConfigConstants.DATABASEHIBERNATE , ConfigConstants.DATABASECASSANDRA);
        object = new Util(new Sender()).dbdrop(dst);
        System.out.println(object);
        object = new Util(new Sender()).dbcopy(src, dst);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(src);
        System.out.println(object);
        object = new Util(new Sender()).dbcheck(dst);
        System.out.println(object);
    }

}
