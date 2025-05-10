package com.chellenge.vpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VppApplication {

	public static void main(String[] args) {
		SpringApplication.run(VppApplication.class, args);
	}

}
