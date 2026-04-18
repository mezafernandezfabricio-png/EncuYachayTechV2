package com.example.Simulador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication(scanBasePackages = {"com.example.SimuladorYachayTech", "com.yachaytech.simulador"})
@EntityScan("com.yachaytech.simulador.models")
@EnableJpaRepositories("com.yachaytech.simulador.repositories")
public class SimuladorYachayTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimuladorYachayTechApplication.class, args);
	}

}
