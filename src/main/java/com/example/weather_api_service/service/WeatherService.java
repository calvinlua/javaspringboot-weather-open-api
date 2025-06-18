package com.example.weather_api_service.service;

import com.example.weather_api_service.model.Weather;
import com.example.weather_api_service.repository.WeatherRepository;
import jakarta.transaction.Transactional; // For transactional database operations
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono; // For reactive programming
import reactor.core.scheduler.Schedulers; // To offload blocking calls in a reactive chain

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class WeatherService {

    private final OpenWeatherMapperService openWeatherMapperService;
    private final WeatherRepository weatherRepository;

    // Constructor Injection
    public WeatherService(OpenWeatherMapperService openWeatherMapperService, WeatherRepository weatherRepository) {
        this.openWeatherMapperService = openWeatherMapperService;
        this.weatherRepository = weatherRepository;
    }

    public Mono<String> getAndStoreWeather(String city, String country) {

        return Mono.defer(() -> {
            Optional<Weather> cachedData = weatherRepository.findByCityAndCountry(city, country);
            if (cachedData.isPresent()) {
                return Mono.just(cachedData.get().getDescription() + " (from cache)");
            } else {
                return openWeatherMapperService.getWeatherDescription(city, country)
                        .flatMap(description -> {

                            return Mono.fromCallable(() -> {
                                Weather weatherData = new Weather(null, city, country, description, LocalDateTime.now());
                                weatherRepository.save(weatherData);
                                return description;
                            }).subscribeOn(Schedulers.boundedElastic());
                        })
                        .onErrorResume(e -> {
                            System.err.println("Failed to get weather data in WeatherService: " + e.getMessage());
                            return Mono.error(new RuntimeException("Failed to get weather data: " + e.getMessage(), e));
                        });
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}