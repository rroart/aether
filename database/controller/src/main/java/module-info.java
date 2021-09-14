/**
 * 
 */
/**
 * @author roart
 *
 */
module database {
    exports roart.database;

    requires common.config;
    requires common.constants;
    requires common.database;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.web;
}