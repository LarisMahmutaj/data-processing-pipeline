package com.laris.dataprocessingpipeline.client.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.laris.dataprocessingpipeline.client.GeocodingClient;
import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.LocationInfo;
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
public class GeocodingClientImpl implements GeocodingClient {
    private final RestTemplate restTemplate;
    private final CityAggregatorProperties properties;

    @Async
    @Override
    public CompletableFuture<@Nullable LocationInfo> getLocationInfo(String cityName) {
        try {
            String url = properties.getApis().getNominatim().getBaseUrl()
                    + "/search?q=" + cityName + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", properties.getApis().getNominatim().getUserAgent());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            NominatimResponse[] responses = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NominatimResponse[].class
            ).getBody();

            if (responses != null && responses.length > 0) {
                NominatimResponse response = responses[0];
                return CompletableFuture.completedFuture(LocationInfo.builder()
                        .latitude(Double.parseDouble(response.getLat()))
                        .longitude(Double.parseDouble(response.getLon()))
                        .country(response.getDisplayName().contains(",")
                                ? response.getDisplayName().substring(response.getDisplayName().lastIndexOf(",") + 1)
                                .trim()
                                : "")
                        .build());
            }

            log.warn("No geocoding results found for city: {}", cityName);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error fetching location info for city: {}", cityName, e);
            return CompletableFuture.completedFuture(null);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NominatimResponse {
        private String lat;
        private String lon;
        @JsonProperty("display_name")
        private String displayName;
    }
}
