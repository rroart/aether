/**
 * 
 */
/**
 * @author roart
 *
 */
module cassandra {
    exports roart.database.cassandra;

    requires com.datastax.oss.driver.core;
    requires com.datastax.oss.driver.mapper.processor;
    requires com.datastax.oss.driver.mapper.runtime;
    requires com.datastax.oss.driver.querybuilder;
    requires java.driver.shaded.guava;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires guava;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.boot.cassandra;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires database;
    //requires junit;
    //requires cassandra.unit;
    //requires cassandra.all;
    //requires libthrift;
}
