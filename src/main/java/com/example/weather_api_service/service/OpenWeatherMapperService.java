package com.example.weather_api_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenWeatherMapperService {

    @Autowired
    private WebClient webClient;

    @Value("${openweathermap.api.key")
    private String openWeatherMapperApiKey;

    private String extractWeatherDescription(JsonNode jsonNode) {
        if (jsonNode != null && jsonNode.has("weather") && jsonNode.get("weather").isArray()) {
            JsonNode weatherArray = jsonNode.get("weather");

            if (!weatherArray.isEmpty() && jsonNode.has("weather") && jsonNode.get("weather").isArray()) {
//            return the description as result as given requirements
                return weatherArray.get(0).get("description").asText();
            }

        }
        return "N/A"; // Give N/A if description is not available
    }

    public Mono<String> getWeatherDescription(String city, String country) {
        String url = String.format("/weather?q=%s,%s&appid=%s", city, country, openWeatherMapperApiKey);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::extractWeatherDescription)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error fetching weather data from OpenWeatherMap: " + e.getMessage(), e)));
    }
}
