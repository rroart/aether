/**
 * 
 */
/**
 * @author roart
 *
 */
module convert {
    exports roart.convert;
    requires common.config;
    requires common.constants;
    requires common.convert;
    requires common.util;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires common.collections;
    requires common.queue;
    requires common.zookeeper;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.boot.web.server;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
    requires curator.framework;
    requires common.model;
    requires curator.client;
}
