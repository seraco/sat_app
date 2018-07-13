package com.seraco.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// tag::code[]
@SpringBootApplication
@EnableScheduling
public class SimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimulatorApplication.class, args);
	}
}
// end::code[]