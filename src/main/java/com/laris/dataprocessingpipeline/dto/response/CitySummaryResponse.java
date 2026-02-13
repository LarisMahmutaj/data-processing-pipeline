package com.laris.dataprocessingpipeline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitySummaryResponse {
    private String cityName;
    private LocationInfo location;
    private String description;
    private WeatherInfo weather;
    private VideoInfo video;
    private boolean hasErrors;
    private List<String> errorMessages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private double latitude;
        private double longitude;
        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInfo {
        private double temperature;
        private String description;
        private int humidity;
        private double windSpeed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoInfo {
        private String videoId;
        private String title;
        private String thumbnailUrl;
        private String videoUrl;
    }
}
