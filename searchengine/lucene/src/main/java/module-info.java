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
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.apache.lucene.core;
    requires org.apache.lucene.highlighter;
    requires org.apache.lucene.queries;
    requires org.apache.lucene.queryparser;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires common.util;
}
