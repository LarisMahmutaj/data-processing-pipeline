package com.laris.dataprocessingpipeline.client.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.laris.dataprocessingpipeline.client.WikipediaClient;
import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class WikipediaClientImpl implements WikipediaClient {
    private final RestTemplate restTemplate;
    private final CityAggregatorProperties properties;

    @Async
    @Override
    public CompletableFuture<@Nullable String> getCityDescription(String cityName) {
        try {
            String url = properties.getApis().getWikipedia().getBaseUrl() 
                + "/page/summary/" + cityName.replace(" ", "_");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", properties.getApis().getNominatim().getUserAgent());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            WikipediaResponse response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                WikipediaResponse.class
            ).getBody();
            
            if (response != null && response.getExtract() != null && !response.getExtract().isEmpty()) {
                return CompletableFuture.completedFuture(response.getExtract());
            }
            
            log.warn("No Wikipedia description found for city: {}", cityName);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error fetching Wikipedia description for city: {}", cityName, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WikipediaResponse {
        private String extract;
    }
}
