package com.client.ws.rasmooplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RasmooplusApplication {

	public static void main(String[] args) {
		SpringApplication.run(RasmooplusApplication.class, args);
	}

}
