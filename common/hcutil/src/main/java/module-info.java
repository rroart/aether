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
    requires common.util;
    requires java.desktop;
    requires java.xml;
    requires org.slf4j;
    requires commons.math3;
    requires org.apache.commons.lang3;
    requires tools.jackson.databind;
    requires tools.jackson.core;
    requires org.apache.commons.io;
    requires common.synchronization;
    requires com.hazelcast.core;
    requires common.config;
    requires common.inmemory.model;
}
