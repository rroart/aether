/**
 * 
 */
/**
 * @author roart
 *
 */
module common.inmemory.message {
    exports roart.common.inmemory.common;
    exports roart.common.inmemory.util;
    
    requires common.inmemory.model;
    requires common.util;
    requires tools.jackson.databind;
    requires tools.jackson.core;
    requires org.apache.commons.codec;
    requires common.constants;
    requires org.slf4j;
    requires tools.jackson.datatype.javatime;
}
