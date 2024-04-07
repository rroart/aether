/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.simple {
    requires java.xml;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.config;
    requires common.util;
    requires common.constants;
    requires common.service;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.apache.commons.lang3;
    requires common.model;
}
