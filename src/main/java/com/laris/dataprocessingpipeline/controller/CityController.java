package com.laris.dataprocessingpipeline.controller;

import com.laris.dataprocessingpipeline.dto.response.CitySummaryResponse;
import com.laris.dataprocessingpipeline.service.CityAggregatorService;
import com.laris.dataprocessingpipeline.service.CityFileReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Slf4j
public class CityController {
    private final CityFileReaderService fileReaderService;
    private final CityAggregatorService aggregatorService;

    @GetMapping("/summary")
    public ResponseEntity<List<CitySummaryResponse>> getCitiesSummary() throws IOException {
        log.info("Received request for cities summary");
        
        List<String> cities = fileReaderService.readCities();
        log.info("Found {} cities to process", cities.size());
        
        List<CitySummaryResponse> summaries = aggregatorService.aggregateCityData(cities);
        
        return ResponseEntity.ok(summaries);
    }
}
