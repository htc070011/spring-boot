package com.bupt.springbootquickstart.config;

import com.bupt.springbootquickstart.service.MyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {

    @Bean
    public MyService myService() {
        return new MyService();
    }
}
