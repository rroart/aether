/**
 * 
 */
/**
 * @author roart
 *
 */
module aether.mahout.mr {
    requires aether.shadow.mahout.mr;
    requires classification;
    requires common.config;
    requires common.constants;
    requires common.machinelearning;
    requires guava;
    requires hadoop.common;
    requires lucene.analyzers.common;
    requires lucene.core;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.cloud.commons;
    requires spring.web;
}
