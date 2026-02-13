package com.laris.dataprocessingpipeline.service;

import com.laris.dataprocessingpipeline.client.GeocodingClient;
import com.laris.dataprocessingpipeline.client.WeatherClient;
import com.laris.dataprocessingpipeline.client.WikipediaClient;
import com.laris.dataprocessingpipeline.client.YouTubeClient;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.LocationInfo;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.VideoInfo;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.WeatherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityAggregatorService {
    private final GeocodingClient geocodingClient;
    private final WeatherClient weatherClient;
    private final WikipediaClient wikipediaClient;
    private final YouTubeClient youtubeClient;

    public List<CitySummaryResponse> aggregateCityData(List<String> cityNames) {
        log.info("Aggregating data for {} cities", cityNames.size());

        List<CompletableFuture<CitySummaryResponse>> futures = cityNames.stream()
                .map(this::aggregateSingleCity)
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        allFutures.join();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private CompletableFuture<CitySummaryResponse> aggregateSingleCity(String cityName) {
        log.debug("Aggregating data for city: {}", cityName);

        List<String> errorMessages = new ArrayList<>();

        CompletableFuture<LocationInfo> locationFuture = geocodingClient.getLocationInfo(cityName)
                .exceptionally(e -> {
                    errorMessages.add("Failed to fetch location: " + e.getMessage());
                    return null;
                });

        CompletableFuture<String> descriptionFuture = wikipediaClient.getCityDescription(cityName)
                .exceptionally(e -> {
                    errorMessages.add("Failed to fetch description: " + e.getMessage());
                    return null;
                });

        CompletableFuture<VideoInfo> videoFuture = youtubeClient.getRelevantVideo(cityName)
                .exceptionally(e -> {
                    errorMessages.add("Failed to fetch video: " + e.getMessage());
                    return null;
                });

        CompletableFuture<WeatherInfo> weatherFuture = locationFuture.thenCompose(loc -> {
            if (loc != null) {
                return weatherClient.getWeatherInfo(loc.getLatitude(), loc.getLongitude())
                        .exceptionally(e -> {
                            errorMessages.add("Failed to fetch weather: " + e.getMessage());
                            return null;
                        });
            } else {
                errorMessages.add("Cannot fetch weather without location data");
                return CompletableFuture.completedFuture(null);
            }
        });

        return CompletableFuture.allOf(locationFuture, descriptionFuture, weatherFuture, videoFuture)
                .thenApply(v -> {
                    LocationInfo location = locationFuture.join();
                    String description = descriptionFuture.join();
                    WeatherInfo weather = weatherFuture.join();
                    VideoInfo video = videoFuture.join();

                    return CitySummaryResponse.builder()
                            .cityName(cityName)
                            .location(location)
                            .description(description)
                            .weather(weather)
                            .video(video)
                            .hasErrors(!errorMessages.isEmpty())
                            .errorMessages(errorMessages)
                            .build();
                });
    }
}
