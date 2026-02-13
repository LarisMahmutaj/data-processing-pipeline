package com.laris.dataprocessingpipeline.pipeline.processor;

import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SortNodeProcessor implements NodeProcessor {

    @Override
    public NodeType getNodeType() {
        return NodeType.SORT;
    }

    @Override
    public List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) {
        String fieldName = (String) configuration.get("field");
        String direction = (String) configuration.getOrDefault("direction", "ASC");

        if (fieldName == null) {
            throw new NodeProcessingException("Field name is required for sort node");
        }

        Comparator<Map<String, Object>> comparator = (doc1, doc2) -> {
            Object v1 = doc1.get(fieldName);
            Object v2 = doc2.get(fieldName);

            if (v1 == null && v2 == null) return 0;
            if (v1 == null) return 1;
            if (v2 == null) return -1;

            int comparison = compareValues(v1, v2);
            return "DESC".equalsIgnoreCase(direction) ? -comparison : comparison;
        };

        return input.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private int compareValues(Object v1, Object v2) {
        if (v1 instanceof Number && v2 instanceof Number) {
            return Double.compare(
                    ((Number) v1).doubleValue(),
                    ((Number) v2).doubleValue()
            );
        }
        return v1.toString().compareTo(v2.toString());
    }
}
