/**
 * 
 */
/**
 * @author roart
 *
 */
module hibernate {
    exports roart.database.hibernate;

    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires database;
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires java.naming;
}
