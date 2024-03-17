/**
 * 
 */
/**
 * @author roart
 *
 */
module common.hcutil {
    exports roart.common.hcutil;

    requires common.constants;
    requires common.model;
    requires common.queue;
    requires java.desktop;
    requires java.xml;
    requires org.slf4j;
    requires commons.math3;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires org.apache.commons.io;
    requires common.synchronization;
    requires com.hazelcast.core;
    requires common.config;
    requires common.inmemory.model;
}
