package com.mohamed.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AlghadeerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlghadeerApplication.class, args);
	}

}
