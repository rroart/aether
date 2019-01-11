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
    requires database;
    requires guava;
    requires slf4j.api;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}