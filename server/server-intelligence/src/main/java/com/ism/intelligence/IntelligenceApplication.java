package com.ism.intelligence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ism.common", "com.ism.intelligence"})
@MapperScan("com.ism.intelligence.mapper")
@EnableScheduling
public class IntelligenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntelligenceApplication.class, args);
    }
}
