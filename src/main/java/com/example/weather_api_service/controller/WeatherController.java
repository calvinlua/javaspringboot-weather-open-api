package com.example.weather_api_service.controller;


import com.example.weather_api_service.model.Weather;
import com.example.weather_api_service.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weathers")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<List<Weather>> getAllWeather() {
        return ResponseEntity.ok(weatherService.getAndStoreWeather());
    }
}
