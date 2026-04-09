package com.example.Encuesta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication(scanBasePackages = {"com.example.EncuestaYachayTech", "com.yachaytech.encuesta"})
@EntityScan("com.yachaytech.encuesta.models")
@EnableJpaRepositories("com.yachaytech.encuesta.repositories")
public class EncuestaYachayTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(EncuestaYachayTechApplication.class, args);
	}

}
