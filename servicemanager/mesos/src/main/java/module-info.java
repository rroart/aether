/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.mesos {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires docker.java;
    requires java.logging;
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
