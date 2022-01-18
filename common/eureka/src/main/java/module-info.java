/**
 * 
 */
/**
 * @author roart
 *
 */
module common.eureka {
    exports roart.eureka.util;

    requires eureka.client;
    requires javax.inject;
    requires org.slf4j;
    requires spring.beans;
    requires spring.cloud.commons;
    requires spring.core;
    requires spring.web;
    requires common.webflux;
}
