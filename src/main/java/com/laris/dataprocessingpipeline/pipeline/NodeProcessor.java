package com.laris.dataprocessingpipeline.pipeline;

import com.laris.dataprocessingpipeline.domain.NodeType;
import com.laris.dataprocessingpipeline.exception.NodeProcessingException;

import java.util.List;
import java.util.Map;

public interface NodeProcessor {

    NodeType getNodeType();

    List<Map<String, Object>> process(
            List<Map<String, Object>> input,
            Map<String, Object> configuration
    ) throws NodeProcessingException;
}
