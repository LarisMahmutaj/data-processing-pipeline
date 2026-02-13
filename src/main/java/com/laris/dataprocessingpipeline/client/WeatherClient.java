package com.laris.dataprocessingpipeline.client;

import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.WeatherInfo;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface WeatherClient {
    CompletableFuture<@Nullable WeatherInfo> getWeatherInfo(double lat, double lon);
}
