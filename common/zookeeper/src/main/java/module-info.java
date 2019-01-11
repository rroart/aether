/**
 * 
 */
/**
 * @author roart
 *
 */
module common.zookeeper {
    exports roart.common.zkutil;

    requires common.constants;
    requires slf4j.api;
    requires zookeeper;
}