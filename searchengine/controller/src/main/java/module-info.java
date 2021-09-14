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
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}