/**
 * 
 */
/**
 * @author roart
 *
 */
module common.communication.message.pulsar {
    exports roart.common.communication.message.pulsar;
    requires common.communication.message.model;
    requires pulsar.client.api;
    //requires pulsar.common;
    requires org.apache.commons.lang3;
    requires tools.jackson.databind;
    requires common.constants;
    requires pulsar.client.admin.api;
    requires org.slf4j;
    //requires com.fasterxml.jackson.annotations;
}
