/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.openshift {
    exports roart.util;
    exports roart.config;
    exports roart.controller;

    //requires docker.client;
    requires docker.dsl;
    requires docker.java;
    requires docker.model;
    requires java.logging;
    requires java.ws.rs;
    requires aether.shadow.fabric8;
    //requires kubernetes.client;
    //requires kubernetes.model;
    //requires openshift.client;
    requires slf4j.api;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.service;
    requires common.constants;
    requires common.config;
    requires common.util;
}
