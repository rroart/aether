/**
 * 
 */
/**
 * @author roart
 *
 */
module calibre {
    requires common.config;
    requires common.constants;
    requires common.convert;
    requires common.model;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires convert;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
    requires org.apache.commons.codec;
    requires curator.framework;
    requires common.util;
    requires common.zookeeper;
}
