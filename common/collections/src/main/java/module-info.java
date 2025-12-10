/**
 * 
 */
/**
 * @author roart
 *
 */
module common.collections {
    exports roart.common.collections;
    exports roart.common.collections.impl;
    
    requires common.communication.factory;
    requires common.inmemory.hazelcast;
    requires common.config;
    requires common.constants;
    requires common.util;
    requires common.hcutil;
    requires curator.client;
    requires curator.framework;
    requires curator.recipes;
    requires com.hazelcast.core;
    requires redis.clients.jedis;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires org.slf4j;
    requires common.communication.model;
    requires redisson;
    requires org.apache.commons.codec;
}
