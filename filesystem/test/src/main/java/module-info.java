/**
 * @author roart
 *
 */
module filesystem.test {
    exports roart.filesystem.test;
    requires curator.framework;
    requires filesystem;
    requires common.config;
    requires org.junit.jupiter.api;
    requires curator.client;
    requires curator.test;
    requires common.filesystem;
    requires common.model;
}
