package com.kedu.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class CsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsProjectApplication.class, args);
	}

}
