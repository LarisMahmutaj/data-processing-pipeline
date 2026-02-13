package com.laris.dataprocessingpipeline.dto.response;

import com.laris.dataprocessingpipeline.domain.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineExecutionResult {
    private Long pipelineId;
    private String pipelineName;
    private boolean success;
    private List<Map<String, Object>> output;
    private List<NodeExecutionLog> executionLogs;
    private String errorMessage;
    private LocalDateTime executedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeExecutionLog {
        private NodeType nodeType;
        private Integer orderIndex;
        private boolean success;
        private String message;
        private int inputCount;
        private int outputCount;
    }
}
