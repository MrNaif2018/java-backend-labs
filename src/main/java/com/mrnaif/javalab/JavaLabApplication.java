package com.mrnaif.javalab;

import com.mrnaif.javalab.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableJpaAuditing // for created field
public class JavaLabApplication {

  public static void main(String[] args) {
    SpringApplication.run(JavaLabApplication.class, args);
  }
}
