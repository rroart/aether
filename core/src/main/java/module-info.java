/**
 * 
 */
/**
 * @author roart
 *
 */
module core {
    requires camel.amqp;
    requires camel.core;
    requires camel.jms;
    requires common.collections;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.eureka;
    requires common.filesystem;
    requires common.machinelearning;
    requires common.model;
    requires common.searchengine;
    requires common.service;
    requires common.synchronization;
    requires common.util;
    requires common.zookeeper;
    requires commons.configuration2;
    requires curator.client;
    requires curator.framework;
    requires curator.recipes;
    //requires eureka.client;
    requires guava;
    requires com.hazelcast.core;
    requires java.desktop;
    requires java.sql;
    requires java.xml;
    requires langdetect;
    requires language.detector;
    requires org.apache.commons.codec;
    //requires org.apache.tika.core;
    //requires org.apache.tika.parsers;
    requires tika.core;
    //requires tika.parsers;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires spring.cloud.netflix.eureka.client;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires zookeeper;
    requires common.inmemory.model;
    requires common.inmemory.factory;
    requires org.apache.commons.io;
}
