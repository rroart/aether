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
public class AnPrIT {

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
    public void my2Test() throws Exception {
        //Object o = new Util(new Sender()).traverse("/home/roart/usr/books/chess/10 Chess Books");
        new Util(new Sender()).traverse("/home/roart/usr/books/computer", null);
        //new Util(new Sender()).index("/home/roart/src/aethermicro/books", false);
    }
    
    @Test
    @Order(2)
    public void my3Test() throws Exception {
        Object o = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true, null, null, null, null);
        System.out.println(o);
    }
    
    @Test
    @Order(3)
    public void my4Test() throws Exception {
        Object o = new Util(new Sender()).search("rook", "0");
        System.out.println(o);
    }
    
    @Test
    @Order(4)
    public void my5Test() throws Exception {
        System.out.println("Ok");
    }
    
    @Test
    @Order(5)
    public void my6Test() throws Exception {
        Object object;
        object = new Util(new Sender()).dbclear(null);
        Object o; // = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true);
        o = new Util(new Sender()).filesystemlucenenew("/home/roart/usr/books/chess", false, null);
        System.out.println(o);
    }
        
    @Test
    @Order(5)
    public void my7Test() throws Exception {
        Object object;
        object = new Util(new Sender()).dbclear(null);
        Object o; // = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true);
        o = new Util(new Sender()).filesystemlucenenew("/home/roart/usr/books/chess/10 Chess Books", false, null);
        System.out.println(o);
    }
    
    @Test
    @Order(5)
    public void my8Test() throws Exception {
        Object object;
        object = new Util(new Sender()).dbclear(null);
        Object o; // = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true);
        o = new Util(new Sender()).filesystemlucenenew("/home/roart/usr/books/chess/xiangqi", false, null);
        System.out.println(o);
    }

    
    @Test
    @Order(5)
    public void my9Test() throws Exception {
        Object object;
        //object = new Util(new Sender()).dbclear(null);
        Object o; // = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true);
        //o = new Util(new Sender()).filesystemlucenenew("/home/roart/usr/books/chess/xiangqi", false);
        o = new Util(new Sender()).index("/home/roart/usr/books/chess/xiangqi", true, null, null, null, null);
        System.out.println(o);
    }

}
