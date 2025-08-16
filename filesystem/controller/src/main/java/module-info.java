/**
 * 
 */
/**
 * @author roart
 *
 */
module filesystem {
    exports roart.filesystem;

    requires common.config;
    requires common.constants;
    requires common.filesystem;
    requires common.collections;
    requires common.queue;
    requires common.zookeeper;
    requires curator.client;
    requires curator.framework;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.web.server;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
    //requires aether.shadow.zookeeper;
    requires common.model;
    requires common.util;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    //requires common.zookeeper;
}
