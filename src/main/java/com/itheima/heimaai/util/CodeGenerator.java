package com.itheima.heimaai.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/ai?serverTimezone=UTC", "root", "5599385")
                .globalConfig(builder -> {
                    builder.author("czd") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.itheima") // 设置父包名
                            .moduleName("heimaai") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("course") // 设置需要生成的表名，多个表用逗号隔开
                            .entityBuilder()
                            .enableLombok() // 启用 Lombok
                            .controllerBuilder()
                            .enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎
                .execute();
    }
}
