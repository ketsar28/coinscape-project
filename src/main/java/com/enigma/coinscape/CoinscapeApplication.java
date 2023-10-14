package com.enigma.coinscape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CoinscapeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoinscapeApplication.class, args);
	}

}
