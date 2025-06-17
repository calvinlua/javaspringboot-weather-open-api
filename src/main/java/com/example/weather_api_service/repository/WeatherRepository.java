package com.example.weather_api_service.repository;

import com.example.weather_api_service.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Initialize Repository interface for JPARepo
public interface WeatherRepository extends JpaRepository<Weather,Long> {

    Optional<Weather> findByCityAndCountry(String city,String country);
}
