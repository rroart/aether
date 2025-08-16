/**
 * 
 */
/**
 * @author roart
 *
 */
module classification {
    exports roart.classification;

    requires common.config;
    requires common.constants;
    requires common.machinelearning;
    requires common.collections;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires common.queue;
    requires common.zookeeper;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.util;
    requires spring.context;
    requires common.model;
    requires curator.framework;
    requires spring.boot;
    requires spring.boot.web.server;
    requires curator.client;
}
