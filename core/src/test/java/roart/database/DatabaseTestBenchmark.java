package roart.database;

import java.util.List;

import org.junit.jupiter.api.Test;

import roart.common.config.ConfigConstants;
import roart.search.Sender;
import roart.search.Util;

/*
 * src database.spring[@enable] dst [28, 697, 24]
src database.spring[@enable] dst [24, 80, 47]
src database.cassandra[@enable] dst [26, 122, 23]
src database.cassandra[@enable] dst [24, 73, 50]
src database.hbase[@enable] dst [36, 121, 22]

database.spring[@enable] null [23, 125, 22] [34, 119, 24]
database.cassandra[@enable] [22, 780, 24] null []
database.hbase[@enable] [23, 71, 46] [25, 73, 35] null
cassandra truncate
 */
public class DatabaseTestBenchmark {
    @Test
    public void myTest() throws Exception {
        List<String> list = List.of(ConfigConstants.DATABASESPRING, ConfigConstants.DATABASECASSANDRA, ConfigConstants.DATABASEHBASE);
        Object[][] bench = new Object[list.size()][list.size()];
        for (int i = 0; i < list.size(); i++) {
            String src = list.get(i);
            for (int j = 0; j < list.size(); j++) {
                if (i == j) {
                    continue;
                }
                String dst = list.get(j);
                //long time0 = System.currentTimeMillis();
                Object object = new Util(new Sender()).dbcopy(src, dst);
                Object[] abench = ((List<Integer>) object).toArray();
                System.out.println("src " + src + " dst " + dst + " " + object);
                //int abench = (int) ((System.currentTimeMillis() - time0) / 1000);
                bench[j][i] = object;
            }
        }
        System.out.print("src");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(" " + list.get(i));
        }
        System.out.println("");
        for (int j = 0; j < list.size(); j++) {
            System.out.print("dst " + list.get(j));
            for (int i = 0; i < list.size(); i++) {
                System.out.print(" " + bench[j][i]);
            }
            System.out.println("");
        }
    }

}
