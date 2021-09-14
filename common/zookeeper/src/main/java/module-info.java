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
    requires org.slf4j;
    requires zookeeper;
}