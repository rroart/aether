/**
 * 
 */
/**
 * @author roart
 *
 */
module searchengine {
    exports roart.search;

    requires common.config;
    requires common.constants;
    requires common.searchengine;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires common.util;
}
