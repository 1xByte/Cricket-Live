package com.panda.CricketLive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CricketLiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(CricketLiveApplication.class, args);
	}

}
