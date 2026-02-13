package com.laris.dataprocessingpipeline;

import com.laris.dataprocessingpipeline.config.CityAggregatorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CityAggregatorProperties.class)
public class DataProcessingPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataProcessingPipelineApplication.class, args);
    }

}
