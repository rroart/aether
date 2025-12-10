/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.mesos {
    requires tools.jackson.core;
    requires tools.jackson.databind;
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
    requires common.model;
}
