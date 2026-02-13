package com.laris.dataprocessingpipeline.client;

import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface WikipediaClient {
    CompletableFuture<@Nullable String> getCityDescription(String cityName);
}
