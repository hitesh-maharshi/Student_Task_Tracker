package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com") // tells Spring to scan all sub-packages
public class ThymeleafDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(ThymeleafDemoApplication.class, args);
	}
}
