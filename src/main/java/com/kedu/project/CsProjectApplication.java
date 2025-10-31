package com.kedu.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.kedu.project.common.Encryptor;

@SpringBootApplication
@EnableScheduling

public class CsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsProjectApplication.class, args);
	}

}
