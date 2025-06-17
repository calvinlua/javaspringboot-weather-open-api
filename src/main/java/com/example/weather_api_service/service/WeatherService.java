package com.example.weather_api_service.service;

import com.example.weather_api_service.model.Weather;
import com.example.weather_api_service.repository.WeatherRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class WeatherService {
    @Autowired
    private OpenWeatherMapperService openWeatherMapperService;
    @Autowired
    private WeatherRepository weatherRepository;

    public Mono<String> getAndStoreWeather(String city, String country) {
        Weather cacheData = weatherRepository.findByCityAndCountry(city, country).orElse(null);

        if (cacheData != null) {
            return Mono.just(cacheData.getDescription() + " (from cache)");
        } else {
            return openWeatherMapperService.getWeatherDescription(city, country)
                    .flatMap(description -> {
                        Weather weatherData = new Weather(null, city, country, description, LocalDateTime.now());
                        weatherRepository.save(weatherData);
                        return Mono.just(description);
                    })
                    .onErrorResume(e -> Mono.error(new RuntimeException("Failed to get weather data: " + e.getMessage(), e)));

        }

    }
}