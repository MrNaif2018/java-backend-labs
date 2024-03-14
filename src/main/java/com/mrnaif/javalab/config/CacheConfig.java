package com.mrnaif.javalab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.mrnaif.javalab.utils.cache.GenericCache;
import com.mrnaif.javalab.utils.cache.SimpleCache;

@Configuration
public class CacheConfig {
    @Bean
    @Scope("prototype")
    public GenericCache cache() {
        return new SimpleCache<>();
    }
}
