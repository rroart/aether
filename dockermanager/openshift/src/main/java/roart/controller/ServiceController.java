package roart.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roart.common.searchengine.SearchEngineSearchParam;
import roart.common.searchengine.SearchEngineSearchResult;
import roart.config.MyConfig;
import roart.service.ServiceParam;
import roart.service.ServiceResult;
import roart.util.EurekaConstants;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceController {

        private Logger log = LoggerFactory.getLogger(this.getClass());

        @RequestMapping(value = "/" + EurekaConstants.SETCONFIG,
                        method = RequestMethod.POST)
        public ServiceResult configDb(@RequestBody ServiceParam param)
                        throws Exception {
                ServiceResult result = new ServiceResult();
                try {
                    // TODO fix
                        //getInstance().config(param.config);
                } catch (Exception e) {
                        log.error(roart.util.Constants.EXCEPTION, e);
                        result.error = e.getMessage();
                }
                return result;
        }

    @RequestMapping(value = "/" + EurekaConstants.GETCONFIG,
            method = RequestMethod.POST)
    public ServiceResult getConfig(@RequestBody ServiceParam param)
            throws Exception {
        ServiceResult result = new ServiceResult();
        try {
            result.config = MyConfig.conf;
        } catch (Exception e) {
            log.error(roart.util.Constants.EXCEPTION, e);
            result.error = e.getMessage();
        }
        return result;
    }


    // TODO move this
    private static void doConfig() {
      System.out.println("config done");
        //log.info("config done");
 
    }
    
        public static void main(String[] args) throws Exception {
            doConfig();
                SpringApplication.run(ServiceController.class, args);
        }

}
