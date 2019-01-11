/**
 * 
 */
/**
 * @author roart
 *
 */
module web {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires common.collections;
    requires common.config;
    requires common.constants;
    requires common.database;
    requires common.eureka;
    requires common.model;
    requires common.searchengine;
    requires common.service;
    requires common.synchronization;
    requires eureka.client;
    requires javax.servlet.api;
    requires jsoup;
    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.cloud.commons;
    requires vaadin.server;
    requires vaadin.shared;
    requires spring.context;
    requires vaadin.spring;
}