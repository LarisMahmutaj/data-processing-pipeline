package com.laris.dataprocessingpipeline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "city-aggregator")
@Data
public class CityAggregatorProperties {
    private String citiesFilePath;
    private ApiConfig apis;

    @Data
    public static class ApiConfig {
        private OpenWeatherMapConfig openweathermap;
        private YouTubeConfig youtube;
        private NominatimConfig nominatim;
        private WikipediaConfig wikipedia;
    }

    @Data
    public static class OpenWeatherMapConfig {
        private String baseUrl;
        private String apiKey;
    }

    @Data
    public static class YouTubeConfig {
        private String baseUrl;
        private String apiKey;
    }

    @Data
    public static class NominatimConfig {
        private String baseUrl;
        private String userAgent;
    }

    @Data
    public static class WikipediaConfig {
        private String baseUrl;
    }
}
