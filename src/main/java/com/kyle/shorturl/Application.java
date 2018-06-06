package com.kyle.shorturl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync
@EnableCaching
@SpringBootApplication
public class Application {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
