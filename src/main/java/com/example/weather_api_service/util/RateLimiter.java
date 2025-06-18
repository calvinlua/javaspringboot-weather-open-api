package com.example.weather_api_service.util;

import org.springframework.stereotype.Component; // Marks this class as a Spring component

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component // Makes this class a Spring bean, so it can be injected (e.g., into ApiKeyService)
public class RateLimiter {
    // Map to store API Key and a map of (timestamp -> count) for calls within an hour
    // Uses ConcurrentHashMap for thread-safety in a multi-threaded environment
    private final Map<String, Map<LocalDateTime,Integer>> apiCallCounts = new ConcurrentHashMap<>();
    private final int MAX_CALLS_PER_HOUR = 5; // Maximum allowed calls per hour for an API key

    /**
     * Checks if a request for the given API key is allowed based on the rate limit.
     * If allowed, it consumes one 'token' for the current hour.
     * @param apiKey The API key for the request.
     * @return true if the request is allowed, false if the rate limit is exceeded.
     */
    public boolean allowRequest(String apiKey){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minus(1, ChronoUnit.HOURS);

        // Ensure an entry exists for the API key
        apiCallCounts.computeIfAbsent(apiKey, k -> new ConcurrentHashMap<>());

        Map<LocalDateTime, Integer> callsForApiKey = apiCallCounts.get(apiKey);

        // Remove calls that are older than one hour (sliding window)
        callsForApiKey.keySet().removeIf(timestamp -> timestamp.isBefore(oneHourAgo));

        // Calculate current calls within the last hour
        int currentCalls = callsForApiKey.values().stream().mapToInt(Integer::intValue).sum();

        if(currentCalls < MAX_CALLS_PER_HOUR) {
            // If within limit, increment call count for the current timestamp
            // Using merge ensures thread-safe increment for the exact same timestamp
            callsForApiKey.merge(now, 1, Integer::sum);
            return true;
        } else {
            return false; // Rate limit exceeded
        }
    }
}