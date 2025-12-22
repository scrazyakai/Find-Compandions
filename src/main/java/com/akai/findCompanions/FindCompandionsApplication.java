package com.akai.findCompanions;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@MapperScan("com.akai.findCompanions.mapper.db")
@EnableScheduling
@EsMapperScan("com.akai.findCompanions.mapper.es")
public class FindCompandionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindCompandionsApplication.class, args);
    }

}
