package com.example.spms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.spms.mapper")
public class   SpmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpmsApplication.class, args);
	}

}
