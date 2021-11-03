/**
 * 
 */
/**
 * @author roart
 *
 */
module dynamodb {
    exports roart.database.dynamodb;

    requires aws.java.sdk.core;
    requires aws.java.sdk.dynamodb;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires database;
    requires guava;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}