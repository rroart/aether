/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.docker {
    requires docker.java;
    requires java.logging;
    requires java.ws.rs;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.constants;
    requires common.config;
    requires common.util;
    requires common.service;
}
