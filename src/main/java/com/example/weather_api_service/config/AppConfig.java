package com.example.weather_api_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

//    Initialize Bean
    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl("http://api.openweathermap.org/data/2.5").build();
    }
}
