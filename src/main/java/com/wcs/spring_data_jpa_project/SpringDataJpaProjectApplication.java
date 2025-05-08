package com.wcs.spring_data_jpa_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringDataJpaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaProjectApplication.class, args);
	}

}
