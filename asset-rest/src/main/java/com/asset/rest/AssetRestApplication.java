package com.asset.rest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.asset.**","com.xiong.**"})
@MapperScan({"com.asset.rest.**.mapper"})
@EnableScheduling
public class AssetRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetRestApplication.class, args);
    }

}
