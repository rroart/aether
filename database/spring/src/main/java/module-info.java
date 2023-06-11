/**
 * 
 */
/**
 * @author roart
 *
 */
module springdata {
    exports roart.database.spring;

    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires database;
    requires java.sql;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires java.naming;
    requires spring.context;
    requires spring.beans;
    requires spring.data.commons;
    requires spring.jdbc;
    requires spring.tx;
    requires spring.data.relational;
    requires spring.data.jdbc;
}
