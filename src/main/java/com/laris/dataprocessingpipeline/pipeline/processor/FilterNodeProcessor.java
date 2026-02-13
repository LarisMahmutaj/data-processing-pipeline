package com.laris.dataprocessingpipeline.pipeline.processor;

import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FilterNodeProcessor implements NodeProcessor {

    @Override
    public NodeType getNodeType() {
        return NodeType.FILTER;
    }

    @Override
    public List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) {
        String fieldName = (String) configuration.get("field");
        String operator = (String) configuration.get("operator");
        Object compareValue = configuration.get("value");

        if (fieldName == null || operator == null) {
            throw new NodeProcessingException("Field name and operator are required for filter node");
        }

        return input.stream()
                .filter(doc -> matchesFilter(doc, fieldName, operator, compareValue))
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(
            Map<String, Object> document,
            String fieldName,
            String operator,
            Object compareValue
    ) {
        Object value = document.get(fieldName);

        if (value == null) {
            return false;
        }

        return switch (operator) {
            case "==" -> value.equals(compareValue);
            case "!=" -> !value.equals(compareValue);
            case "<" -> compareNumbers(value, compareValue) < 0;
            case ">" -> compareNumbers(value, compareValue) > 0;
            case "<=" -> compareNumbers(value, compareValue) <= 0;
            case ">=" -> compareNumbers(value, compareValue) >= 0;
            case "CONTAINS" -> value.toString().contains(compareValue.toString());
            case "STARTS_WITH" -> value.toString().startsWith(compareValue.toString());
            case "ENDS_WITH" -> value.toString().endsWith(compareValue.toString());
            default -> false;
        };
    }

    private int compareNumbers(Object v1, Object v2) {
        if (!(v1 instanceof Number) || !(v2 instanceof Number)) {
            throw new NodeProcessingException("Cannot compare non-numeric values with numeric operators");
        }
        double num1 = ((Number) v1).doubleValue();
        double num2 = ((Number) v2).doubleValue();
        return Double.compare(num1, num2);
    }
}
