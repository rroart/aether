package roart.common.hcutil;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import roart.common.config.NodeConfig;
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
            HazelcastConfig.getHazelcastSerializationConfig(config.getSerializationConfig());
            config.getNetworkConfig().addAddress(server).setSmartRouting(false);;
            hz = HazelcastClient.newHazelcastClient(config);
        }
        return hz;
    }
    
    public static HazelcastInstance instance(NodeConfig nodeConf) {
        final HazelcastInstance hz;
        if (nodeConf.isInmemoryServerHazelcast()) {
            hz = GetHazelcastInstance.instance(nodeConf.getInmemoryHazelcast());
        } else {
            hz = null;
        }
        return hz;
    }
    public static synchronized HazelcastInstance serverInstance() {
        if (true) {
            Config config = new Config();
            HazelcastConfig.getHazelcastSerializationConfig(config.getSerializationConfig());
            config.getJetConfig().setEnabled(true);
            // check
            String appid = System.getenv(Constants.APPID);
            if (appid != null) {
                //config.setClusterName(appid);
            }

            config.getCPSubsystemConfig().setCPMemberCount(3);
            hz = Hazelcast.newHazelcastInstance(config);
            log.info("Creating Hazelcast server instance");
        }
        return hz;
    }
}
