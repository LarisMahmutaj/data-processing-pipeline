package com.laris.dataprocessingpipeline.dto.response;

import com.laris.dataprocessingpipeline.domain.NodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineNodeResponse {
    private Long id;
    private NodeType nodeType;
    private Integer orderIndex;
    private Map<String, Object> configuration;
    private LocalDateTime createdAt;
}