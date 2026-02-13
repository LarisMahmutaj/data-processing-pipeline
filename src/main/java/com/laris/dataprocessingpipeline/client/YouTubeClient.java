package com.laris.dataprocessingpipeline.client;

import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse.VideoInfo;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface YouTubeClient {
    CompletableFuture<@Nullable VideoInfo> getRelevantVideo(String cityName);
}
