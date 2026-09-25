package org.example.azoi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;   // ← 这个导入

@SpringBootApplication
@EnableScheduling
public class AzoiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzoiApplication.class, args);
    }

}
