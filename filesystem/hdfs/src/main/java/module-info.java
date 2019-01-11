/**
 * 
 */
/**
 * @author roart
 *
 */
module hdfs {
    exports roart.filesystem.hdfs;

    requires common.config;
    requires common.constants;
    requires common.filesystem;
    requires common.model;
    requires commons.io;
    requires filesystem;
    requires hadoop.common;
    requires slf4j.api;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}