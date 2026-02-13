package com.laris.dataprocessingpipeline.pipeline.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laris.dataprocessingpipeline.context.UserContext;
import com.laris.dataprocessingpipeline.domain.ExportedData;
import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import com.laris.dataprocessingpipeline.repository.ExportedDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExportNodeProcessor implements NodeProcessor {

    private final ObjectMapper objectMapper;
    private final ExportedDataRepository exportedDataRepository;

    @Override
    public NodeType getNodeType() {
        return NodeType.EXPORT;
    }

    @Override
    public List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) {
        String destinationType = (String) configuration.get("destinationType");

        if (destinationType == null) {
            destinationType = "return";
        }

        switch (destinationType) {
            case "file":
                exportToFile(input, configuration);
                break;
            case "database":
                exportToDatabase(input, configuration);
                break;
            case "return":
                break;
            default:
                throw new NodeProcessingException("Unknown destination type: " + destinationType);
        }

        return input;
    }

    private void exportToFile(List<Map<String, Object>> data, Map<String, Object> config) {
        String filePath = (String) config.get("filePath");
        if (filePath == null || filePath.isEmpty()) {
            throw new NodeProcessingException("File path is required for file export");
        }

        try {
            objectMapper.writeValue(new File(filePath), data);
        } catch (IOException e) {
            throw new NodeProcessingException("Failed to export to file: " + e.getMessage(), e);
        }
    }

    private void exportToDatabase(List<Map<String, Object>> data, Map<String, Object> config) {
        String exportKey = (String) config.get("exportKey");
        if (exportKey == null || exportKey.isEmpty()) {
            throw new NodeProcessingException("Export key is required for database export");
        }

        try {
            String jsonData = objectMapper.writeValueAsString(data);

            ExportedData exportedData = ExportedData.builder()
                    .exportKey(exportKey)
                    .data(jsonData)
                    .createdBy(UserContext.getCurrentUser())
                    .build();

            exportedDataRepository.save(exportedData);
        } catch (IOException e) {
            throw new NodeProcessingException("Failed to serialize data for database export: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new NodeProcessingException("Failed to export to database: " + e.getMessage(), e);
        }
    }
}
