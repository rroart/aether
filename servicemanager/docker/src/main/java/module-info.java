/**
 * 
 */
/**
 * @author roart
 *
 */
module servicemanager.docker {
    exports roart.util;
    exports roart.config;
    exports roart.controller;

    requires docker.java;
    requires java.logging;
    requires java.ws.rs;
    requires slf4j.api;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.constants;
    requires common.config;
    requires common.util;
    requires common.service;
}