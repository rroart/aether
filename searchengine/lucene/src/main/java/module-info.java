/**
 * 
 */
/**
 * @author roart
 *
 */
module lucene {
    exports roart.search.lucene;

    requires searchengine;
    requires common.config;
    requires common.constants;
    requires common.searchengine;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires lucene.core;
    requires lucene.highlighter;
    requires lucene.queries;
    requires lucene.queryparser;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}
