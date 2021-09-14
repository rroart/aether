/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.mesos {
    exports roart.util;
    exports roart.config;
    exports roart.controller;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires docker.java;
    requires java.logging;
    requires java.ws.rs;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.core;
    requires spring.web;
    requires common.constants;
    requires common.config;
    requires common.util;
    requires common.service;
}