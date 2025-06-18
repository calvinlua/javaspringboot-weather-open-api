package com.example.weather_api_service.service;

import com.example.weather_api_service.util.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ApiKeyService {

    private final Set<String> validApiKeys = new HashSet<>();
    private final RateLimiter rateLimiter;

    // Constructor
    public ApiKeyService(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;

        validApiKeys.add("APIKEY-001");
        validApiKeys.add("APIKEY-002");
        validApiKeys.add("APIKEY-003");
        validApiKeys.add("APIKEY-004");
        validApiKeys.add("APIKEY-005");

    }


    public boolean isValidApiKey(String apiKey){
        return validApiKeys.contains(apiKey);
    }

    public boolean isRateLimitExceeded(String apiKey){
        return !rateLimiter.allowRequest(apiKey);
    }
}
