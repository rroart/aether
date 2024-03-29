package roart.convert.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.curator.framework.CuratorFramework;

import static org.mockito.Mockito.any;

import roart.common.convert.ConvertResult;

public class TikaTest {

    @Test
    public void test() {
        CuratorFramework curatorClient = null;
        Tika tika = new Tika(null, null, null, curatorClient );
        doReturn(convert2(null)).when(tika).convert2(any());
        ConvertResult result = tika.convert(null);
        System.out.println(result);
    }
    
    public ConvertResult convert2(Object[] param2) {
        System.out.println("Convert2");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        return null;
    }
}
