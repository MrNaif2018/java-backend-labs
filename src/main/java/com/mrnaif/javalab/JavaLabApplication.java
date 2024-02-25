package com.mrnaif.javalab;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.mrnaif.javalab.config.StorageProperties;
import com.mrnaif.javalab.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableJpaAuditing // for created field
public class JavaLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaLabApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return args -> storageService.init();
	}

}
