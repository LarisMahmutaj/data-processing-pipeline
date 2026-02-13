package com.laris.dataprocessingpipeline.client.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.laris.dataprocessingpipeline.client.WeatherClient;
import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.WeatherInfo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherClientImpl implements WeatherClient {
    private final RestTemplate restTemplate;
    private final CityAggregatorProperties properties;

    @Async
    @Override
    public CompletableFuture<@Nullable WeatherInfo> getWeatherInfo(double lat, double lon) {
        try {
            String apiKey = properties.getApis().getOpenweathermap().getApiKey();

            String url = properties.getApis().getOpenweathermap().getBaseUrl()
                    + "/weather?lat=" + lat + "&lon=" + lon
                    + "&appid=" + apiKey + "&units=metric";

            OpenWeatherResponse response = restTemplate.getForObject(url, OpenWeatherResponse.class);

            if (response != null && response.getMain() != null) {
                return CompletableFuture.completedFuture(WeatherInfo.builder()
                        .temperature(response.getMain().getTemp())
                        .description(response.getWeather() != null && !response.getWeather().isEmpty()
                                ? response.getWeather().get(0).getDescription()
                                : "")
                        .humidity(response.getMain().getHumidity())
                        .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : 0.0)
                        .build());
            }

            log.warn("No weather data found for coordinates: {}, {}", lat, lon);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error fetching weather info for coordinates: {}, {}", lat, lon, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenWeatherResponse {
        private MainData main;
        private List<WeatherDescription> weather;
        private WindData wind;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MainData {
        private double temp;
        private int humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WeatherDescription {
        private String description;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WindData {
        private double speed;
    }
}
