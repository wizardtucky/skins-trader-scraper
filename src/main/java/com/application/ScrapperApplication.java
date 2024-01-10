package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScrapperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapperApplication.class, args);
	}
}
