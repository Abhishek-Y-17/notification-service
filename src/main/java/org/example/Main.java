package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.example.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "org.example.repository.elastic")
@EnableTransactionManagement
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }
}