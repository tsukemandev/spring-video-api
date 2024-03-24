package com.streamdiaries.springvideoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringVideoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringVideoApiApplication.class, args);
	}

}
