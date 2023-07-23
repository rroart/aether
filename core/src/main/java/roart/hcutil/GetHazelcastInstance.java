package roart.hcutil;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import roart.common.constants.Constants;

import com.hazelcast.config.Config;

public class GetHazelcastInstance {
    static HazelcastInstance hz = null;
    public static synchronized HazelcastInstance instance() {
        if (hz == null) {
            Config config = HazelcastConfig.getHazelcastConfig();
            String appid = System.getenv(Constants.APPID);
            if (appid != null) {
                config.setClusterName(appid);
            }

            //config.getCPSubsystemConfig().setCPMemberCount(3);
            hz = Hazelcast.newHazelcastInstance(config);
        }
        return hz;
    }
}
