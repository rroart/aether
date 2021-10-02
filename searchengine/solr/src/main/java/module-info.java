/**
 * 
 */
/**
 * @author roart
 *
 */
module solr {
    exports roart.search.solr;

    requires searchengine;
    requires common.config;
    requires common.constants;
    requires common.searchengine;
    requires org.slf4j;
    //requires solr.core;
    requires solr.solrj;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
}
