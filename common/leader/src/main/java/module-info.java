/**
 * 
 */
/**
 * @author roart
 *
 */
module common.leader {
    exports roart.common.leader;
    exports roart.common.leader.impl;
    
    requires common.config;
    requires common.constants;
    requires common.util;
    requires common.zookeeper;
    requires curator.client;
    requires curator.framework;
    requires curator.recipes;
    requires com.hazelcast.core;
    requires redis.clients.jedis;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
}
