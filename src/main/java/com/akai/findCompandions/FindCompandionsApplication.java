package com.akai.findCompandions;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.akai.findCompandions.mapper")
@EnableScheduling
public class FindCompandionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindCompandionsApplication.class, args);
    }

}
