package com.example.weather_api_service.controller;

import com.example.weather_api_service.service.ApiKeyService;
import com.example.weather_api_service.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono; // Import for reactive programming with WebFlux

@RestController
@RequestMapping("/api/v1/weathers") // Base path for weather-related endpoints
public class WeatherController {

    private final WeatherService weatherService;
    private final ApiKeyService apiKeyService;

    public WeatherController(WeatherService weatherService, ApiKeyService apiKeyService) {
        this.weatherService = weatherService;
        this.apiKeyService = apiKeyService;
    }
    @GetMapping("/{city}/{country}")
    public Mono<ResponseEntity<String>> getWeatherByCityAndCountry(
            @PathVariable String city,      // Extracts 'city' from the URL path
            @PathVariable String country,   // Extracts 'country' from the URL path
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey // Extracts API key from request header
    ) {

        if (city == null || city.trim().isEmpty() || country == null || country.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("City and country parameters are required."));
        }


        if (apiKey == null || apiKey.trim().isEmpty()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API Key is missing. Please provide X-API-KEY header."));
        }
        if (!apiKeyService.isValidApiKey(apiKey)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key."));
        }


        if (apiKeyService.isRateLimitExceeded(apiKey)) {
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Hourly rate limit exceeded for this API Key. Please try again later."));
        }


        return weatherService.getAndStoreWeather(city, country)
                .map(description -> ResponseEntity.ok("Weather Description: " + description)) // Map the description to a success response
                .onErrorResume(e -> { // Handle any errors in the reactive stream
                    // Log the error for debugging purposes (consider using SLF4J/Logback)
                    System.err.println("Error in WeatherController: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error retrieving weather: " + e.getMessage()));
                });
    }

}