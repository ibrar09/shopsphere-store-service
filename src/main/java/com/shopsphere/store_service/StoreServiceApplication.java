package com.shopsphere.store_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableDiscoveryClient
@EnableSpringDataWebSupport
@EnableJpaAuditing
public class StoreServiceApplication {

    public static void main(String[] args) {
       SpringApplication.run(StoreServiceApplication.class, args);
    }
}
