/**
 * 
 */
/**
 * @author roart
 *
 */
module tika {
    requires common.config;
    requires common.constants;
    requires common.convert;
    requires common.model;
    requires common.inmemory.message;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires convert;
    requires org.apache.tika.core;
    //requires org.apache.tika.parsers;
    //requires tika.core;
    //requires tika.parsers;
    requires org.slf4j;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.context;
    requires spring.web;
    requires org.apache.commons.codec;
    requires java.xml;
    requires curator.framework;
    requires common.util;
    requires common.zookeeper;
    requires org.apache.tika.parser.ocr;
    requires org.apache.tika.parser.pdf;
}

