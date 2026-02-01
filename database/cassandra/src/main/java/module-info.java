/**
 * 
 */
/**
 * @author roart
 *
 */
module cassandra {
    exports roart.database.cassandra;

    requires java.driver.core;
    requires java.driver.mapper.runtime;
    requires java.driver.query.builder;
    requires java.driver.shaded.guava;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.boot.cassandra;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires database;
}
