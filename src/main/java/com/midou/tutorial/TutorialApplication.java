package com.midou.tutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TutorialApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TutorialApplication.class, args);


    }
}