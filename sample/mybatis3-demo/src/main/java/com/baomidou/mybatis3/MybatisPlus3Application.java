package com.baomidou.mybatis3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan("com.baomidou.mybatis3.mapper,generator.mapper")
@ImportResource({"classpath:applicationContext.xml"})
public class MybatisPlus3Application {

}
