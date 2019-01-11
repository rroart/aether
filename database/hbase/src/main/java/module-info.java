/**
 * 
 */
/**
 * @author roart
 *
 */
module hbase {
    requires aether.shadow.hbase;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires database;
    requires hadoop.common;
    requires slf4j.api;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
}
