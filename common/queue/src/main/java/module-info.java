/**
 * 
 */
/**
 * @author roart
 *
 */
module common.queue {
    exports roart.common.queue;

    requires com.fasterxml.jackson.annotation;
    requires common.config;
    requires common.model;
    requires common.service;
    requires common.constants;
    requires common.database;
    requires common.filesystem;
    requires common.machinelearning;
    requires common.convert;
    requires common.searchengine;
    requires org.slf4j;
    requires common.inmemory.model;
}
