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
    requires slf4j.api;
    requires spring.beans;
    requires spring.cloud.commons;
    requires spring.core;
    requires spring.web;
}