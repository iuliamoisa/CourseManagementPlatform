package com.ing.hubs.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Slf4j
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ing.hubs.jpa.repository")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}