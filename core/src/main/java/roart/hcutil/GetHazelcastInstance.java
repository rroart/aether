package roart.hcutil;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import roart.common.constants.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;

public class GetHazelcastInstance {
    protected static Logger log = LoggerFactory.getLogger(GetHazelcastInstance.class);
    static HazelcastInstance hz = null;
    public static synchronized HazelcastInstance instance(String server) {
        if (hz == null) {
            ClientConfig config = new ClientConfig();
            config.getNetworkConfig().addAddress(server);
            hz = HazelcastClient.newHazelcastClient(config);
        }
        if (true) return hz;
        if (hz == null) {
            Config config = HazelcastConfig.getHazelcastConfig();
            config.getJetConfig().setEnabled(true);
            // check
            String appid = System.getenv(Constants.APPID);
            if (appid != null) {
                //config.setClusterName(appid);
            }

            //config.getCPSubsystemConfig().setCPMemberCount(3);
            hz = Hazelcast.newHazelcastInstance(config);
            log.info("Creating Hazelcast instance");
        }
        return hz;
    }
}
