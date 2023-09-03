package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.example.controller","com.example.model","com.example.service"})
@EnableJpaRepositories("com.example.*")
@EntityScan("com.example.*")
public class CoronaVirusTracking1Application {

	public static void main(String[] args) {
		SpringApplication.run(CoronaVirusTracking1Application.class, args);
	}

}
