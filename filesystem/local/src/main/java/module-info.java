/**
 * 
 */
/**
 * @author roart
 *
 */
module local {
    exports roart.filesystem.local;

    requires common.config;
    requires common.constants;
    requires common.filesystem;
    requires common.model;
    requires commons.io;
    requires filesystem;
    requires slf4j.api;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
}