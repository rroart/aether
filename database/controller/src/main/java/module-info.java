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
    requires common.collections;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires com.hazelcast.core;
    requires spring.context;
}
