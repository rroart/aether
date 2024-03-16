package roart.common.inmemory.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;

public class GetHazelcastInstance {
    private static Logger log = LoggerFactory.getLogger(GetHazelcastInstance.class);
    static HazelcastInstance hz = null;
    public static HazelcastInstance instance(String server) {
        if (hz == null) {
            log.info("Server {}", server);
            ClientConfig config = new ClientConfig();
            config.getNetworkConfig().addAddress(server).setSmartRouting(false);;
            hz = HazelcastClient.newHazelcastClient(config);
        }
        return hz;
    }
}
