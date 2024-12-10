package com.example.banthing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@MapperScan("com.example.banthing.domain.item.mapper")
public class BanThingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanThingApplication.class, args);
    }

}
