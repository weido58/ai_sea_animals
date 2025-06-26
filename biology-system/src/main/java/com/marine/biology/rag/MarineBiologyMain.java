package com.marine.biology.rag;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarineBiologyMain {

    public static void main(String[] args) {

        SpringApplication.run(MarineBiologyMain.class,args);
        System.out.println("""
            ====================================
            🛫 RAG系统启动成功！
            ====================================
            访问地址: http://localhost:8080/index.html
            ====================================
            """);
    }
}
