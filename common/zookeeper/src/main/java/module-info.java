/**
 * 
 */
/**
 * @author roart
 *
 */
module common.zookeeper {
    exports roart.common.zkutil;
    exports roart.common.zk.thread;

    requires common.constants;
    requires org.slf4j;
    requires aether.shadow.zookeeper;
}
