package roart.filesystem;

import org.junit.Test;

import roart.config.ConfigConstants;
import roart.config.NodeConfig;
import roart.model.FileObject;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.model.Account;
import org.junit.Before;

public class SwiftTest {
    
    private SwiftConfig conf;
    private Swift swift;
    
   @Before
    public void setup() {
       try {
       conf = new SwiftConfig();
       String url = "http://172.17.0.2:8080/auth/v1.0";
       String username = "test:tester";
       String password = "doowimihiree";
       //log.info("INFO " + url + " " + username + "  " + password);
       if (url != null) {
           AccountConfig config;
           config = new AccountConfig();
           config.setUsername( username);
           config.setPassword(password);
           config.setAuthUrl(url);
           config.setAuthenticationMethod(AuthenticationMethod.BASIC);
           Account account = new AccountFactory(config).createAccount();
           conf.account = account;
           swift = new Swift();
           swift.conf = conf;
           //log.info("here");
       }
   } catch (Exception e) {
       //log.error("Exception", e);
       //return null;
   }
        
    }
    
    @Test
    public void test() {
        FileSystemPathParam paramp = new FileSystemPathParam();
        getParamConf(paramp);
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        getParamConf(param);
        paramp.path = "xiangqi";
        param.fo = new FileObject("xiangqi", "Swift");
        FileSystemFileObjectResult get = swift.get(paramp);
        System.out.println("r " + get.fileObject);
        param.fo = get.fileObject[0];
        FileSystemBooleanResult ex = swift.exists(param);
        System.out.println("Ex " + ex.bool);
    }
    
    @Test
    public void test2() {
        FileSystemFileObjectParam param = new FileSystemFileObjectParam();
        FileSystemPathParam paramp = new FileSystemPathParam();
        getParamConf(param);
        getParamConf(paramp);
        paramp.path = ".";
        FileSystemFileObjectResult res;
        FileSystemFileObjectResult resp;
        resp = swift.get(paramp);
        param.fo = resp.fileObject[0];
        res = swift.listFiles(param);
        if (res != null) {
        System.out.println("l1 " + res.fileObject.length);
        }
        paramp.path = "/";
        resp = swift.get(paramp);
        //param.fo = new FileObject("/");
        param.fo = resp.fileObject[0];
        res = swift.listFiles(param);
        if (res != null) {
        System.out.println("l2 " + res.fileObject.length + " " + res.fileObject[0].object);
        }
        paramp.path = "xiangqi";
        resp = swift.get(paramp);
        //param.fo = new FileObject("xiangqi");
        param.fo = resp.fileObject[0];
        res = swift.listFiles(param);
        if (res != null) {
        System.out.println("l3 " + res.fileObject.length);
        }
        paramp.path = "swift:/chess/xiangqi/";
        resp = swift.get(paramp);
        //param.fo = new FileObject("xiangqi");
        param.fo = resp.fileObject[0];
        res = swift.listFiles(param);
        if (res != null) {
        System.out.println("l4 " + res.fileObject.length+ " " + res.fileObject[0].object);
        }
    }

    private void getParamConf(FileSystemParam param) {
        param.conf = new NodeConfig();
        param.conf.configValueMap = new HashMap<>();
        param.conf.configValueMap.put(ConfigConstants.FILESYSTEMSWIFTSWIFTCONFCONTAINER, "chess");
    }
}
