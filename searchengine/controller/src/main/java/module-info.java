/**
 * 
 */
/**
 * @author roart
 *
 */
module searchengine {
    exports roart.search;

    requires common.config;
    requires common.constants;
    requires common.searchengine;
    requires common.collections;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires common.queue;
    requires common.zookeeper;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires common.util;
    requires curator.framework;
    requires common.model;
    requires curator.client;
    requires spring.beans;
}
