/**
 * 
 */
/**
 * @author roart
 *
 */
module spark.ml {
    exports roart.classification.spark.ml;

    requires common.machinelearning;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires common.config;
    requires common.constants;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires classification;
    requires aether.shadow.spark;
    requires common.util;
}
