/**
 * 
 */
/**
 * @author roart
 *
 */
module elastic {
    exports roart.search.elastic;

    requires common.config;
    requires common.constants;
    requires common.searchengine;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires searchengine;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires aether.shadow.elastic;
    requires org.apache.httpcomponents.httpcore;
}
