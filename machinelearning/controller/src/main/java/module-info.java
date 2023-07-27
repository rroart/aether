/**
 * 
 */
/**
 * @author roart
 *
 */
module classification {
    exports roart.classification;

    requires common.config;
    requires common.constants;
    requires common.machinelearning;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires common.util;
}
