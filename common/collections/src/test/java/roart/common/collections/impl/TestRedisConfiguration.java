package roart.common.collections.impl;

import java.io.IOException;

import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.context.Configuration;
import org.springframework.context.annotation.Configuration;

import redis.embedded.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
//@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration() throws IOException {
        this.redisServer = new RedisServer(6378); // TODO port
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() throws IOException {
        redisServer.stop();
    }
}