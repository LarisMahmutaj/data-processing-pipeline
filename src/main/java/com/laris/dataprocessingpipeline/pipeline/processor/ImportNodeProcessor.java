package com.laris.dataprocessingpipeline.pipeline.processor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laris.dataprocessingpipeline.domain.ExportedData;
import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import com.laris.dataprocessingpipeline.repository.ExportedDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImportNodeProcessor implements NodeProcessor {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExportedDataRepository exportedDataRepository;

    @Override
    public NodeType getNodeType() {
        return NodeType.IMPORT;
    }

    @Override
    public List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) {
        String sourceType = (String) configuration.get("sourceType");

        if (sourceType == null) {
            throw new NodeProcessingException("Source type is required for import node");
        }

        return switch (sourceType) {
            case "http" -> importFromHttp(configuration);
            case "file" -> importFromFile(configuration);
            case "database" -> importFromDatabase(configuration);
            default -> throw new NodeProcessingException("Unknown source type: " + sourceType);
        };
    }

    private List<Map<String, Object>> importFromHttp(Map<String, Object> config) {
        String url = (String) config.get("url");
        if (url == null || url.isEmpty()) {
            throw new NodeProcessingException("URL is required for HTTP import");
        }

        try {
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new NodeProcessingException("Failed to import from HTTP: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> importFromFile(Map<String, Object> config) {
        String filePath = (String) config.get("filePath");
        if (filePath == null || filePath.isEmpty()) {
            throw new NodeProcessingException("File path is required for file import");
        }

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new NodeProcessingException("File not found: " + filePath);
            }
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (NodeProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new NodeProcessingException("Failed to import from file: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> importFromDatabase(Map<String, Object> config) {
        String exportKey = (String) config.get("exportKey");
        if (exportKey == null || exportKey.isEmpty()) {
            throw new NodeProcessingException("Export key is required for database import");
        }

        try {
            ExportedData exportedData = exportedDataRepository
                    .findFirstByExportKeyOrderByExportedAtDesc(exportKey)
                    .orElseThrow(() -> new NodeProcessingException(
                            "No exported data found with key: " + exportKey));

            return objectMapper.readValue(exportedData.getData(), new TypeReference<>() {
            });
        } catch (NodeProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new NodeProcessingException("Failed to import from database: " + e.getMessage(), e);
        }
    }
}
