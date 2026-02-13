package com.laris.dataprocessingpipeline.client;

import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.LocationInfo;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface GeocodingClient {
    CompletableFuture<@Nullable LocationInfo> getLocationInfo(String cityName);
}
