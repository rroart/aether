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
    requires common.filesystem;
    requires org.slf4j;
    requires common.inmemory.model;
}
