package com.shopsphere.store_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSpringDataWebSupport
public class StoreServiceApplication {

    public static void main(String[] args) {
       SpringApplication.run(StoreServiceApplication.class, args);
    }
}
