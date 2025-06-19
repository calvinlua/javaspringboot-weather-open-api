package com.example.weather_api_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono; // For reactive programming

@Service
public class OpenWeatherMapperService {

    private final WebClient webClient;

    // Injects the API key from application.properties
    @Value("${openweathermap.api.key}")
    private String openWeatherMapperApiKey;

    public OpenWeatherMapperService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Method to extract weather description from the JSON response
    private String extractWeatherDescription(JsonNode jsonNode) {
        if (jsonNode != null && jsonNode.has("weather") && jsonNode.get("weather").isArray()) {
            JsonNode weatherArray = jsonNode.get("weather");
            if (!weatherArray.isEmpty() && weatherArray.get(0).has("description")) {
                return weatherArray.get(0).get("description").asText();
            }
        }
        return "N/A"; // Return "N/A" if description is not found
    }

    // Method to get weather description from OpenWeatherMap API
    public Mono<String> getWeatherDescription(String city, String country) {
        String url = String.format("/weather?q=%s,%s&appid=%s", city, country, openWeatherMapperApiKey);

        // Use WebClient to make a GET request to the OpenWeatherMap API
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractWeatherDescription)
                .onErrorResume(e -> {
                    System.err.println("Error fetching weather data from OpenWeatherMap: " + e.getMessage());
                    return Mono.error(new RuntimeException("Error fetching weather data from OpenWeatherMap: " + e.getMessage(), e));
                });
    }
}