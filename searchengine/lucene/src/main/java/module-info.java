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
    requires lucene.core;
    requires lucene.highlighter;
    requires lucene.queries;
    requires lucene.queryparser;
    requires slf4j.api;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}
