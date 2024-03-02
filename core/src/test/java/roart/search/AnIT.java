package roart.search;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.constants.EurekaConstants;
import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.common.service.ServiceParam;
import roart.common.service.ServiceParam.Function;
import roart.common.service.ServiceResult;
import roart.eureka.util.EurekaUtil;

public class AnIT {

    @Test
    public void myTest() {
        
    }
    
    @Test
    @Order(0)
    public void my0Test() throws Exception {
        new Util(new Sender()).indexclean();
        new Util(new Sender()).dbclear(null);
    }
    
    @Test
    @Order(1)
    public void myDbTest() throws Exception {
        new Util(new Sender()).dbclear(null);
        Object o = new Util(new Sender()).traverse(null, null);
        System.out.println(o);
    }
    
    @Test
    @Order(1)
    public void my1Test() throws Exception {
        Object o = new Util(new Sender()).traverse(null, null);
        System.out.println(o);
        new Util(new Sender()).index(null, false, null, null, null, null);
    }
    
    @Test
    @Order(2)
    public void my2Test() throws Exception {
        Object o = new Util(new Sender()).traverse("/home/roart/src/aethermicro/books", null);
        System.out.println(o);
        new Util(new Sender()).index("/home/roart/src/aethermicro/books", false, null, null, null, null);
    }
    
    @Test
    @Order(3)
    public void my3Test() throws Exception {
        new Util(new Sender()).index("/home/roart/src/aethermicro/books", true, null, null, null, null);
    }
    
    @Test
    @Order(3)
    public void my33Test() throws Exception {
        new Util(new Sender()).index(null, true, null, null, null, null);
    }
    
    @Test
    @Order(4)
    public void my4Test() throws Exception {
        Object l = new Util(new Sender()).search("amazon", "0");
        System.out.println(l);
        l = new Util(new Sender()).search("lucene", "0");
        System.out.println(l);
        l = new Util(new Sender()).search("music", "0");
        System.out.println(l);
    }
    
    @Test
    @Order(42)
    public void my41Test() throws Exception {
        Object l = new Util(new Sender()).searchmlt("0000b8dd7fd8004c1df08a363187c3ff");
        System.out.println(l);
    }
    
    @Test
    @Order(5)
    public void my5Test() throws Exception {
        System.out.println("Ok");
    }
    
    @Test
    @Order(6)
    public void my6Test() throws Exception {
        new Util(new Sender()).index("/home/roart/src/aethermicro/books", true, null, null, null, null);
    }
    
    @Test
    public void dTest() throws Exception {
        new Util(new Sender()).deletepathdb("/home/roart/usr/aethermicro/books/doc");
    }
     
}
