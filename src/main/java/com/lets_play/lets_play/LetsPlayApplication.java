package com.lets_play.lets_play;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LetsPlayApplication {
 // used to run the embedded server 
	public static void main(String[] args) {
		// passes a reference
		SpringApplication.run(LetsPlayApplication.class, args);
	}

}


        