package com.transaction.statistics.chellenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChellengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChellengeApplication.class, args);
	}
}
