package com.example.weather_api_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    public WebClient webClient() {
        // Base URL for OpenWeatherMap API
        return WebClient.builder().baseUrl("http://api.openweathermap.org/data/2.5").build();
    }
}