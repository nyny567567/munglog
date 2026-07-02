package com.munglog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MunglogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MunglogApplication.class, args);
	}

}
