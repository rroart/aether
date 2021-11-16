/**
 * 
 */
/**
 * @author roart
 *
 */
module opennlp {
    exports roart.classification.opennlp;

    requires classification;
    requires common.config;
    requires common.constants;
    requires common.machinelearning;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    //requires opennlp.tools;
    requires org.slf4j;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
    requires org.apache.opennlp.tools;
}
