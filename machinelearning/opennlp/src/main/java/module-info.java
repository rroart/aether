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
    requires opennlp.tools;
    requires slf4j.api;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
    requires spring.boot;
}