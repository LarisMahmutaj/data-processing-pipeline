package com.laris.dataprocessingpipeline.pipeline.processor;

import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;
import com.laris.dataprocessingpipeline.pipeline.NodeProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransformNodeProcessor implements NodeProcessor {

    @Override
    public NodeType getNodeType() {
        return NodeType.TRANSFORM;
    }

    @Override
    public List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) {
        String fieldName = (String) configuration.get("field");
        String operation = (String) configuration.get("operation");
        Object operand = configuration.get("operand");

        if (fieldName == null || operation == null) {
            throw new NodeProcessingException("Field name and operation are required for transform node");
        }

        return input.stream()
                .map(doc -> transformDocument(doc, fieldName, operation, operand))
                .collect(Collectors.toList());
    }

    private Map<String, Object> transformDocument(
            Map<String, Object> document,
            String fieldName,
            String operation,
            Object operand
    ) {
        Object value = document.get(fieldName);
        Object transformedValue = applyTransformation(value, operation, operand);

        Map<String, Object> result = new HashMap<>(document);
        result.put(fieldName, transformedValue);
        return result;
    }

    private Object applyTransformation(Object value, String operation, Object operand) {
        if (value == null) {
            return null;
        }

        if (value instanceof String str) {
            return switch (operation) {
                case "uppercase" -> str.toUpperCase();
                case "lowercase" -> str.toLowerCase();
                case "trim" -> str.trim();
                case "replace" -> {
                    if (operand instanceof Map) {
                        Map<String, String> replaceConfig = (Map<String, String>) operand;
                        String from = replaceConfig.get("from");
                        String to = replaceConfig.get("to");
                        yield str.replace(from, to);
                    }
                    yield value;
                }
                default -> value;
            };
        }

        if (value instanceof Number) {
            double num = ((Number) value).doubleValue();
            double operandNum = operand != null ? ((Number) operand).doubleValue() : 0;

            return switch (operation) {
                case "add" -> num + operandNum;
                case "subtract" -> num - operandNum;
                case "multiply" -> num * operandNum;
                case "divide" -> operandNum != 0 ? num / operandNum : num;
                case "round" -> Math.round(num);
                case "pow" -> Math.pow(num, operandNum);
                default -> value;
            };
        }

        if (value instanceof List<?> list) {
            return switch (operation) {
                case "sort" -> list.stream().sorted().collect(Collectors.toList());
                case "removeDuplicates" -> list.stream().distinct().collect(Collectors.toList());
                case "length" -> list.size();
                default -> value;
            };
        }

        return value;
    }
}
