package com.lihainuo.imgify.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.lihainuo.imgify.server.mapper")
public class ImgifyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImgifyServerApplication.class, args);
    }

}
