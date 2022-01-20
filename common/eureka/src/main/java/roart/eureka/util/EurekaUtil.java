package roart.eureka.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.shared.Application;

import roart.common.webflux.WebFluxUtil;

@EnableDiscoveryClient
public class EurekaUtil {

    private static Logger log = LoggerFactory.getLogger(EurekaUtil.class);

    private static ApplicationInfoManager applicationInfoManager;
    public static EurekaClient eurekaClient = null;
    
    @Autowired
    public static DiscoveryClient discoveryClient;

    public static EurekaClient initEurekaClient() {
        log.info("here");
        if (discoveryClient == null) {
            // create the client
            ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
            initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());
        } else {
            log.info("Got discovery client");
        }
        if (eurekaClient != null) {
            log.info("euClient " + eurekaClient.getAllKnownRegions());
            List<Application> apps = eurekaClient.getApplications().getRegisteredApplications();
            for (Application app : apps) {
                log.info("currently available app " + app.getName()); 
            }
        }
        //discoveryClient = DiscoveryManager.getInstance().getDiscoveryClient();		
        if (discoveryClient != null) {
            List<Application> apps = discoveryClient.getApplications().getRegisteredApplications();
            for (Application app : apps) {
                System.out.println("currently available app2 " + app.getName()); 
                log.info("currently available app2 " + app.getName()); 
            }
        }
        try {
            // more example code:
            /*
			String vipAddress = "LUCENE";
			InstanceInfo nextServerInfo = DiscoveryManager.getInstance()
					.getEurekaClient()
					.getNextServerFromEureka(vipAddress, false);
			log.info("Found an instance of example service to talk to from eureka: "
					+ nextServerInfo.getVIPAddress() + ":" + nextServerInfo.getPort());

			log.info("healthCheckUrl: " + nextServerInfo.getHealthCheckUrl());
			log.info("override: " + nextServerInfo.getOverriddenStatus());

			log.info("Server Host Name "+ nextServerInfo.getHostName() + " at port " + nextServerInfo.getPort() );		

			log.info("conf " + discoveryClient.getEurekaClientConfig().getEurekaServerPort());
             */
        } catch (Exception e) {
            log.error("Cannot get an instance of example service to talk to from eureka");
        }
        return eurekaClient;
    }

    public static <T> T sendMe(Class<T> myclass, Object param, String appName, String path) {

        String homePageUrl = null;
        log.debug("homePagePre " + appName + " " + path);
        log.debug("clis" + eurekaClient + " " + discoveryClient);
        if (discoveryClient != null) {
            List<InstanceInfo> li = discoveryClient.getApplication(appName).getInstances();
            for (InstanceInfo ii : li) {
                log.debug("homePage " + ii.getHomePageUrl());
            }
            if (!li.isEmpty()) {
                homePageUrl = li.get(0).getHomePageUrl();
            }
        }
        if (homePageUrl == null && eurekaClient != null) {
            List<InstanceInfo> li = eurekaClient.getApplication(appName).getInstances();
            for (InstanceInfo ii : li) {
                log.debug("homePage2 " + ii.getHomePageUrl());
            }
            if (!li.isEmpty()) {
                homePageUrl = li.get(0).getHomePageUrl();
            }
        }
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(param, headers);
        String url = homePageUrl;
        if (true) {
            return WebFluxUtil.sendMe(myclass, url, param, path);
        }
        RestTemplate rt = new RestTemplate();
        /*
		for (HttpMessageConverter<?> converter : rt.getMessageConverters()) {
			log.info(converter.getClass().getName());
		}
		log.info(rt.getMessageConverters().size());
         */
        ResponseEntity<T> regr = new RestTemplate().postForEntity(url + path, request, myclass);
        T result = regr.getBody();
        return result;
    }

    public static <T> T sendMe(Class<T> myclass, String url, Object param, String path) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        HttpEntity<?> request = new HttpEntity<>(param, headers);
        RestTemplate rt = new RestTemplate();
        log.info("uuu " + url + path);
        ResponseEntity<T> regr = new RestTemplate().postForEntity(url + path, request, myclass);
        T result = regr.getBody();
        return result;
    }

    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }
        return applicationInfoManager;
    }

    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        if (eurekaClient == null) {
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }
        return eurekaClient;
    }

}
