package com.laris.dataprocessingpipeline.service;

import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityFileReaderService {
    private final CityAggregatorProperties properties;

    public List<String> readCities() throws IOException {
        Path path = Paths.get(properties.getCitiesFilePath());
        log.info("Reading cities from file: {}", path);
        
        return Files.readAllLines(path)
            .stream()
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toList());
    }
}
