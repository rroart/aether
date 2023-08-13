package roart.common.inmemory.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;

public class GetHazelcastInstance {
    static HazelcastInstance hz = null;
    public static HazelcastInstance instance(String server) {
        if (hz == null) {
            ClientConfig config = new ClientConfig();
            config.getNetworkConfig().addAddress(server);
            hz = HazelcastClient.newHazelcastClient(config);
        }
        return hz;
    }
}
