package sn.agriculteur.marche_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MarcheServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarcheServiceApplication.class, args);
	}

}
