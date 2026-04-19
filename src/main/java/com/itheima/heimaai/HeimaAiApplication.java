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
        var context = SpringApplication.run(HeimaAiApplication.class, args);

        // 验证数据库连接
        try {
            DataSource dataSource = context.getBean(DataSource.class);
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✅ 数据库连接成功！当前数据库是：" + connection.getMetaData().getDatabaseProductName());
                System.out.println("🔗 连接地址：" + connection.getMetaData().getURL());
            }
        } catch (Exception e) {
            System.err.println("❌ 数据库连接失败！报错信息：" + e.getMessage());
        }
    }
}
