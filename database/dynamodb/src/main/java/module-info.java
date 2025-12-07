/**
 * 
 */
/**
 * @author roart
 *
 */
module dynamodb {
    exports roart.database.dynamodb;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.model;
    requires common.util;
    requires database;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.services.dynamodb;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.awscore;
}
