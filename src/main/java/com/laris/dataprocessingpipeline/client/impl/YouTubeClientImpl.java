package com.laris.dataprocessingpipeline.client.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laris.dataprocessingpipeline.client.YouTubeClient;
import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.VideoInfo;
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
public class YouTubeClientImpl implements YouTubeClient {
    private final RestTemplate restTemplate;
    private final CityAggregatorProperties properties;

    @Async
    @Override
    public CompletableFuture<@Nullable VideoInfo> getRelevantVideo(String cityName) {
        try {
            String apiKey = properties.getApis().getYoutube().getApiKey();

            String url = properties.getApis().getYoutube().getBaseUrl()
                    + "/search?part=snippet&q=" + cityName + "+tour&type=video&maxResults=1&key=" + apiKey;

            YouTubeResponse response = restTemplate.getForObject(url, YouTubeResponse.class);

            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                YouTubeItem item = response.getItems().get(0);
                String videoId = item.getId().getVideoId();
                return CompletableFuture.completedFuture(VideoInfo.builder()
                        .videoId(videoId)
                        .title(item.getSnippet().getTitle())
                        .thumbnailUrl(item.getSnippet().getThumbnails().getDefaultThumbnail().getUrl())
                        .videoUrl("https://youtube.com/watch?v=" + videoId)
                        .build());
            }

            log.warn("No YouTube video found for city: {}", cityName);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error fetching YouTube video for city: {}", cityName, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class YouTubeResponse {
        private List<YouTubeItem> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class YouTubeItem {
        private VideoId id;
        private Snippet snippet;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class VideoId {
        private String videoId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Snippet {
        private String title;
        private Thumbnails thumbnails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Thumbnails {
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Thumbnail {
        private String url;
    }
}
