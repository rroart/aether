/**
 * 
 */
/**
 * @author roart
 *
 */
module common.model {
    exports roart.common.model;

    requires com.fasterxml.jackson.annotation;
    requires common.config;
    requires common.constants;
    requires common.synchronization;
    requires common.inmemory.model;
    requires org.slf4j;
}
