/**
 * 
 */
/**
 * @author roart
 *
 */
module database {
    exports roart.database;

    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.util;
    requires common.queue;
    requires common.collections;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires common.zookeeper;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.context;
    requires common.model;
    requires curator.client;
    requires curator.framework;
    requires spring.beans;
}
