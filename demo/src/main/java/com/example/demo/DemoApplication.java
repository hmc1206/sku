package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Auto Configuration(자동 설정)
@SpringBootApplication
public class DemoApplication {
	//내장 웹 서버(Embedded WAS)
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
