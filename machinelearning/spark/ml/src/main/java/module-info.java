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
    requires classification;
    requires aether.shadow.spark;
}