/**
 * 
 */
/**
 * @author roart
 *
 */
module mahout.spark {
    exports roart.classification.mahout.spark;

    requires aether.shadow.mahout.spark;
    requires aether.shadow.mahout.sparkcore;
    requires common.config;
    requires common.constants;
    requires common.machinelearning;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires lucene.core;
    requires scala.library;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires classification;
    requires hadoop.common;
    requires com.google.common;
}
