package com.mrnaif.javalab;

import static org.assertj.core.api.Assertions.assertThat;

import com.mrnaif.javalab.config.StorageProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@EnableConfigurationProperties(StorageProperties.class)
class JavaLabApplicationTests {

  @Test
  void contextLoads(ApplicationContext context) {
    assertThat(context).isNotNull();
  }
}
