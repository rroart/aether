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
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
}