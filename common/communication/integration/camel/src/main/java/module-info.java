/**
 * 
 */
/**
 * @author roart
 *
 */
module common.communication.integration.camel {
    exports roart.common.communication.integration.camel;
    requires camel.api;
    requires camel.core.engine;
    requires common.communication.integration.model;
    requires common.util;
    requires camel.amqp;
    requires tools.jackson.databind;
    requires org.apache.commons.lang3;
    requires common.constants;
    requires org.slf4j;
    requires camel.spring.rabbitmq;
    requires com.rabbitmq.client;
    requires spring.rabbit;
    requires spring.amqp;
}
