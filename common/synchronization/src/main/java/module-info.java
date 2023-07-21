/**
 * 
 */
/**
 * @author roart
 *
 */
module common.synchronization {
    exports roart.common.synchronization;
    exports roart.common.synchronization.impl;

    requires common.constants;
    requires curator.framework;
    requires curator.recipes;
    requires com.hazelcast.core;
    //requires slf4j.api;
    requires org.slf4j;
}
