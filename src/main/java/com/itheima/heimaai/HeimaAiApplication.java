package com.itheima.heimaai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
@MapperScan("com.itheima.heimaai.mapper")
public class HeimaAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HeimaAiApplication.class, args);
    }
}
