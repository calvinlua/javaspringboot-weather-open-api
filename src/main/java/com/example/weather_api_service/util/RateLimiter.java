package com.example.weather_api_service.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    // Map to store API Key and a map of (timestamp -> count) for calls within an hour
    private final Map<String, Map<LocalDateTime,Integer>> apiCallCounts = new ConcurrentHashMap<>();
    private final int MAX_CALLS_PER_HOUR = 5;

    public boolean allowRequest(String apiKey){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minus(1, ChronoUnit.HOURS);

        apiCallCounts.computeIfAbsent(apiKey,k -> new ConcurrentHashMap<>());

        Map<LocalDateTime, Integer> callsForApiKey = apiCallCounts.get(apiKey);

        //Remove calls older than one hour
        callsForApiKey.keySet().removeIf(timestamp -> timestamp.isBefore(oneHourAgo));
        int currentCalls = callsForApiKey.values().stream().mapToInt(Integer::intValue).sum();

        if(currentCalls < MAX_CALLS_PER_HOUR) {
            // Increment call count for the current minute/second to avoid excessive map entries
            // For simplicity, we just add a new entry for 'now'. A more robust solution might group by minute.
            callsForApiKey.merge(now, 1, Integer::sum);
            return true;
        } else {
            return false;
        }
    }
}

