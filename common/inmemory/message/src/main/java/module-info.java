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
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.commons.codec;
    requires common.constants;
    requires org.slf4j;
}
